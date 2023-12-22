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

import io.flamingock.core.cloud.lock.CloudLockService;
import io.flamingock.core.cloud.planner.client.ExecutionPlannerClient;
import io.flamingock.core.cloud.transaction.CloudLocalStatus;
import io.flamingock.core.cloud.transaction.CloudLocalStater;
import io.flamingock.core.configurator.core.CoreConfigurable;
import io.flamingock.core.configurator.core.ServiceId;
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

import java.util.List;

public class CloudExecutionPlanner extends ExecutionPlanner {

    private static final Logger logger = LoggerFactory.getLogger(CloudExecutionPlanner.class);

    private final CoreConfigurable coreConfiguration;

    private final CloudLockService lockService;

    private final TimeService timeService;

    private final RunnerId runnerId;

    private final ExecutionPlannerClient client;

    private final ServiceId serviceId;

    private final CloudLocalStater cloudLocalStater;

    public CloudExecutionPlanner(ServiceId serviceId,
                                 RunnerId runnerId,
                                 ExecutionPlannerClient client,
                                 CoreConfigurable coreConfiguration,
                                 CloudLockService lockService,
                                 CloudLocalStater cloudLocalStater,
                                 TimeService timeService) {
        this.client = client;
        this.serviceId = serviceId;
        this.runnerId = runnerId;
        this.coreConfiguration = coreConfiguration;
        this.lockService = lockService;
        this.cloudLocalStater = cloudLocalStater;
        this.timeService = timeService;
    }

    @Override
    protected ExecutionPlan getNextExecution(Pipeline pipeline) throws LockException {

        List<LoadedStage> loadedStages = pipeline.getLoadedStages();

        //In every execution, as it start a stopwatch
        StopWatch stopWatch = StopWatch.getNoStarted();
        ThreadSleeper lockThreadSleeper = new ThreadSleeper(
                coreConfiguration.getLockQuitTryingAfterMillis(),
                coreConfiguration.getLockTryFrequencyMillis(),
                stopWatch,
                LockException::new
        );
        String lastOwnerGuid = null;
        do {
            try {
                ExecutionPlanResponse response = createExecution(loadedStages, lastOwnerGuid, stopWatch.getElapsed());

                if (response.isContinue()) {
                    return ExecutionPlan.CONTINUE();

                } else if (response.isExecute()) {
                    return buildNextExecutionPlan(loadedStages, response);

                } else if (response.isAwait()) {
                    if (lastOwnerGuid == null || !lastOwnerGuid.equals(response.getLock().getAcquisitionId())) {
                        //if the lock's guid has been changed, the stopwatch needs to be reset
                        stopWatch.reset();
                    }
                    stopWatch.run();
                    lastOwnerGuid = response.getLock().getAcquisitionId();
                    lockThreadSleeper.checkThresholdAndWait(
                            response.getLock().getAcquiredForMillis() - stopWatch.getElapsed()
                    );

                } else {
                    throw new RuntimeException("Unrecognized action from response. Not within(CONTINUE, EXECUTE, AWAIT)");
                }

            } catch (Throwable exception) {
                logger.warn(exception.getMessage());
                lockThreadSleeper.checkThresholdAndWait();
            }
        } while (true);
    }

    private ExecutionPlanResponse createExecution(List<LoadedStage> loadedStages, String lastAcquisitionId, long elapsedMillis) {

        ExecutionPlanRequest requestBody = ExecutionPlanMapper.toRequest(
                loadedStages,
                coreConfiguration.getLockAcquiredForMillis(),
                getLocalStatus());

        ExecutionPlanResponse responsePlan = client.createExecution(
                serviceId, runnerId, requestBody, lastAcquisitionId, elapsedMillis);
        responsePlan.validate();
        return responsePlan;
    }

    private CloudLocalStatus getLocalStatus() {
        return cloudLocalStater != null ? cloudLocalStater.getStatus().orElse(null) : null;
    }

    private ExecutionPlan buildNextExecutionPlan(List<LoadedStage> loadedStages, ExecutionPlanResponse response) {
        return ExecutionPlan.newExecution(
                ExecutionPlanMapper.extractLockFromResponse(response, coreConfiguration, runnerId, lockService, timeService),
                ExecutionPlanMapper.getExecutableStages(response, loadedStages)
        );
    }

}
