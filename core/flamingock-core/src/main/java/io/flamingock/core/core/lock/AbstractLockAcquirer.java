package io.flamingock.core.core.lock;

import io.flamingock.core.core.audit.AuditReader;
import io.flamingock.core.core.audit.domain.AuditStageStatus;
import io.flamingock.core.core.stage.ExecutableStage;
import io.flamingock.core.core.stage.LoadedStage;
import io.flamingock.core.core.util.TimeService;

public abstract class AbstractLockAcquirer<AUDIT_PROCESS_STATE extends AuditStageStatus, EXECUTABLE_PROCESS extends ExecutableStage>
        implements LockAcquirer<AUDIT_PROCESS_STATE, EXECUTABLE_PROCESS> {

    private final AuditReader<AUDIT_PROCESS_STATE> auditReader;

    public AbstractLockAcquirer(AuditReader<AUDIT_PROCESS_STATE> auditReader) {
        this.auditReader = auditReader;
    }

    @Override
    public LockAcquisition acquireIfRequired(LoadedStage<AUDIT_PROCESS_STATE, EXECUTABLE_PROCESS> loadedStage,
                                             LockOptions lockOptions) throws LockException {
        AUDIT_PROCESS_STATE currentAuditProcessStatus = auditReader.getAuditProcessStatus();
        EXECUTABLE_PROCESS executableProcess = loadedStage.applyState(currentAuditProcessStatus);
        if (executableProcess.doesRequireExecution()) {
            Lock lock = acquireLock(lockOptions);
            if(lockOptions.isWithDaemon()) {
                new LockRefreshDaemon(lock, new TimeService()).start();
            }
            return new LockAcquisition.Acquired(lock);
        } else {
            return new LockAcquisition.NoRequired();
        }
    }

    protected abstract Lock acquireLock(LockOptions options);

}
