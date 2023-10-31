package io.flamingock.core.lock;

import io.flamingock.core.audit.AuditReader;
import io.flamingock.core.audit.domain.AuditStageStatus;
import io.flamingock.core.pipeline.ExecutableStage;
import io.flamingock.core.pipeline.LoadedStage;
import io.flamingock.core.util.TimeService;

public abstract class AbstractLockAcquirer implements LockAcquirer {

    private final AuditReader auditReader;

    public AbstractLockAcquirer(AuditReader auditReader) {
        this.auditReader = auditReader;
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

    protected abstract Lock acquireLock(LockOptions options);

}
