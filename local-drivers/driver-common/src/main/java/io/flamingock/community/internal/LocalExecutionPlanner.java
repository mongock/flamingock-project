/*
 * Copyright 2023 Flamingock (https://oss.flamingock.io)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.flamingock.community.internal;


import io.flamingock.community.internal.lock.LocalLock;
import io.flamingock.community.internal.lock.LockRepository;
import io.flamingock.core.configurator.CoreConfigurable;
import io.flamingock.core.driver.audit.AuditReader;
import io.flamingock.core.driver.audit.writer.AuditStageStatus;
import io.flamingock.core.driver.execution.Execution;
import io.flamingock.core.driver.execution.ExecutionPlanner;
import io.flamingock.core.driver.lock.Lock;
import io.flamingock.core.driver.lock.LockException;
import io.flamingock.core.driver.lock.LockOptions;
import io.flamingock.core.driver.lock.LockRefreshDaemon;
import io.flamingock.core.pipeline.ExecutableStage;
import io.flamingock.core.pipeline.Pipeline;
import io.flamingock.core.pipeline.Stage;
import io.flamingock.core.util.TimeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.stream.Collectors;

public class LocalExecutionPlanner implements ExecutionPlanner {
    private static final Logger logger = LoggerFactory.getLogger(LocalExecutionPlanner.class);

    private final AuditReader auditReader;
    private final LockRepository lockRepository;

    private final CoreConfigurable configuration;


    /**
     * @param lockRepository    lockRepository to persist the lock
     * @param auditReader
     * @param coreConfiguration
     */
    public LocalExecutionPlanner(LockRepository lockRepository,
                                 AuditReader auditReader,
                                 CoreConfigurable coreConfiguration) {
        this.auditReader = auditReader;
        this.lockRepository = lockRepository;
        this.configuration = coreConfiguration;
    }

    @Override
    public Execution getNextExecution(Pipeline pipeline, LockOptions lockOptions) throws LockException {
        AuditStageStatus currentAuditStageStatus = auditReader.getAuditStageStatus();
        logger.debug("Pulled remote state:\n{}", currentAuditStageStatus);

        List<ExecutableStage> executableStages = pipeline
                .getStages()
                .stream()
                .map(Stage::load)
                .map(loadedStage -> loadedStage.applyState(currentAuditStageStatus))
                .collect(Collectors.toList());

        if (executableStages.stream().anyMatch(ExecutableStage::doesRequireExecution)) {
            Lock lock = acquireLock(lockOptions);
            if (lockOptions.isWithDaemon()) {
                new LockRefreshDaemon(lock, new TimeService()).start();
            }
            return Execution.newExecution(lock, executableStages);

        } else {
            return Execution.CONTINUE();
        }
    }

    private Lock acquireLock(LockOptions lockOptions) {
        return LocalLock.getLock(
                configuration.getLockAcquiredForMillis(),
                configuration.getLockQuitTryingAfterMillis(),
                configuration.getLockTryFrequencyMillis(),
                lockOptions.getOwner(),
                lockRepository,
                new TimeService()
        );
    }

}
