/*
 * Copyright 2023 Flamingock ("https://oss.flamingock.io")
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.flamingock.core.cloud.planner;

import io.flamingock.core.api.exception.FlamingockException;
import io.flamingock.core.cloud.lock.CloudLockService;
import io.flamingock.core.cloud.planner.client.ExecutionPlannerClient;
import io.flamingock.core.cloud.transaction.OngoingStatus;
import io.flamingock.core.cloud.transaction.OngoingStatusRepository;
import io.flamingock.core.configurator.core.CoreConfigurable;
import io.flamingock.core.configurator.core.ServiceId;
import io.flamingock.core.engine.audit.domain.AuditItem;
import io.flamingock.core.engine.execution.ExecutionPlan;
import io.flamingock.core.engine.execution.ExecutionPlanner;
import io.flamingock.core.engine.lock.LockException;
import io.flamingock.core.pipeline.LoadedStage;
import io.flamingock.core.pipeline.Pipeline;
import io.flamingock.core.runner.RunnerId;
import io.flamingock.core.util.StopWatch;
import io.flamingock.core.util.ThreadSleeper;
import io.flamingock.core.util.TimeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class CloudExecutionPlanner extends ExecutionPlanner {

    private static final Logger logger = LoggerFactory.getLogger(CloudExecutionPlanner.class);

    private final CoreConfigurable coreConfiguration;

    private final CloudLockService lockService;

    private final TimeService timeService;

    private final RunnerId runnerId;

    private final ExecutionPlannerClient client;

    private final ServiceId serviceId;

    private final OngoingStatusRepository ongoingStatusRepository;

    public CloudExecutionPlanner(ServiceId serviceId,
                                 RunnerId runnerId,
                                 ExecutionPlannerClient client,
                                 CoreConfigurable coreConfiguration,
                                 CloudLockService lockService,
                                 OngoingStatusRepository ongoingStatusRepository,
                                 TimeService timeService) {
        this.client = client;
        this.serviceId = serviceId;
        this.runnerId = runnerId;
        this.coreConfiguration = coreConfiguration;
        this.lockService = lockService;
        this.ongoingStatusRepository = ongoingStatusRepository;
        this.timeService = timeService;
    }

    @Override
    protected ExecutionPlan getNextExecution(Pipeline pipeline) throws LockException {

        List<LoadedStage> loadedStages = pipeline.getLoadedStages();

        //In every execution, as it start a stopwatch
        ThreadSleeper lockThreadSleeper = new ThreadSleeper(
                coreConfiguration.getLockQuitTryingAfterMillis(),
                LockException::new
        );
        String lastOwnerGuid = null;
        StopWatch counterPerGuid = StopWatch.getNoStarted();
        do {
            try {
                logger.info("Requesting cloud execution plan - elapsed[{}ms]", counterPerGuid.getElapsed());
                ExecutionPlanResponse response = createExecution(loadedStages, lastOwnerGuid, counterPerGuid.getElapsed());
                logger.info("Obtained cloud execution plan: {}", response.getAction());
                if (response.isContinue()) {
                    return ExecutionPlan.CONTINUE();

                } else if (response.isExecute()) {
                    return buildNextExecutionPlan(loadedStages, response);

                } else if (response.isAwait()) {
                    if (lastOwnerGuid == null || !lastOwnerGuid.equals(response.getLock().getAcquisitionId())) {
                        //if the lock's guid has been changed, the stopwatch needs to be reset
                        logger.info(
                                "counter per lock GUID re-started: lastOwnerGuid[{}] and response guid[{}] - elapsed[{}ms]",
                                lastOwnerGuid,
                                response.getLock().getAcquisitionId(),
                                counterPerGuid.getElapsed());
                        counterPerGuid.reset();
                    }
                    lastOwnerGuid = response.getLock().getAcquisitionId();
                    long remainingTimeForSameGuid = response.getLock().getAcquiredForMillis() - counterPerGuid.getElapsed();
                    logger.info("AWAIT response from server - elapsed[{}ms]", counterPerGuid.getElapsed());
                    lockThreadSleeper.checkThresholdAndWait(
                            Math.min(remainingTimeForSameGuid, coreConfiguration.getLockTryFrequencyMillis())
                    );

                } else {
                    throw new RuntimeException("Unrecognized action from response. Not within(CONTINUE, EXECUTE, AWAIT)");
                }

            } catch (FlamingockException ex) {
                logger.warn("Error after elapsed[{}ms]", counterPerGuid.getElapsed());
                throw ex;
            } catch (Throwable exception) {
                throw new FlamingockException(exception);
            }
        } while (true);
    }

    private ExecutionPlanResponse createExecution(List<LoadedStage> loadedStages, String lastAcquisitionId, long elapsedMillis) {

        Map<String, AuditItem.Operation> ongoingStatusesMap = getOngoingStatuses()
                .stream()
                .collect(Collectors.toMap(OngoingStatus::getTaskId, OngoingStatus::getOperation));

        ExecutionPlanRequest requestBody = ExecutionPlanMapper.toRequest(
                loadedStages,
                coreConfiguration.getLockAcquiredForMillis(),
                ongoingStatusesMap);

        ExecutionPlanResponse responsePlan = client.createExecution(
                serviceId, runnerId, requestBody, lastAcquisitionId, elapsedMillis);
        responsePlan.validate();
        return responsePlan;
    }

    private Collection<OngoingStatus> getOngoingStatuses() {
        return ongoingStatusRepository != null ? ongoingStatusRepository.getOngoingStatuses() : Collections.emptySet();
    }

    private ExecutionPlan buildNextExecutionPlan(List<LoadedStage> loadedStages, ExecutionPlanResponse response) {
        return ExecutionPlan.newExecution(
                ExecutionPlanMapper.extractLockFromResponse(response, coreConfiguration, runnerId, lockService, timeService),
                ExecutionPlanMapper.getExecutableStages(response, loadedStages)
        );
    }

}
