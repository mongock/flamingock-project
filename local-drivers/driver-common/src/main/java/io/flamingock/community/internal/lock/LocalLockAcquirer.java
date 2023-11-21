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

package io.flamingock.community.internal.lock;


import io.flamingock.core.driver.audit.AuditReader;
import io.flamingock.core.configurator.CoreConfigurable;
import io.flamingock.core.driver.audit.writer.AuditStageStatus;
import io.flamingock.core.driver.lock.Lock;
import io.flamingock.core.driver.lock.LockAcquirer;
import io.flamingock.core.driver.lock.LockAcquisition;
import io.flamingock.core.driver.lock.LockException;
import io.flamingock.core.driver.lock.LockOptions;
import io.flamingock.core.driver.lock.LockRefreshDaemon;
import io.flamingock.core.pipeline.ExecutableStage;
import io.flamingock.core.pipeline.LoadedStage;
import io.flamingock.core.util.TimeService;

public class LocalLockAcquirer implements LockAcquirer {

    private final AuditReader auditReader;
    private final LockRepository lockRepository;

    private final CoreConfigurable configuration;


    /**
     * @param lockRepository    lockRepository to persist the lock
     * @param auditReader
     * @param coreConfiguration
     */
    public LocalLockAcquirer(LockRepository lockRepository,
                             AuditReader auditReader,
                             CoreConfigurable coreConfiguration) {
        this.auditReader = auditReader;
        this.lockRepository = lockRepository;
        this.configuration = coreConfiguration;
    }


    @Override
    public LockAcquisition acquireIfRequired(LoadedStage loadedStage, LockOptions lockOptions) throws LockException {
        AuditStageStatus currentAuditStageStatus = auditReader.getAuditStageStatus();
        ExecutableStage executableStage = loadedStage.applyState(currentAuditStageStatus);
        if (executableStage.doesRequireExecution()) {
            Lock lock = acquireLock(lockOptions);
            if (lockOptions.isWithDaemon()) {
                new LockRefreshDaemon(lock, new TimeService()).start();
            }
            return new LockAcquisition.Acquired(lock);
        } else {
            return new LockAcquisition.NoRequired();
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
