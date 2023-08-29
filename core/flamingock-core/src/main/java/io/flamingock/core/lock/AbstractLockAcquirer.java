package io.flamingock.core.lock;

import io.flamingock.core.audit.single.SingleAuditReader;
import io.flamingock.core.audit.single.SingleAuditStageStatus;
import io.flamingock.core.pipeline.stage.ExecutableStage;
import io.flamingock.core.pipeline.stage.LoadedStage;
import io.flamingock.core.util.TimeService;

public abstract class AbstractLockAcquirer implements LockAcquirer {

    private final SingleAuditReader auditReader;

    public AbstractLockAcquirer(SingleAuditReader auditReader) {
        this.auditReader = auditReader;
    }

    @Override
    public LockAcquisition acquireIfRequired(LoadedStage loadedStage, LockOptions lockOptions) throws LockException {
        SingleAuditStageStatus currentAuditStageStatus = auditReader.getAuditStageStatus();
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
