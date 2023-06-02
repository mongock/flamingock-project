package io.flamingock.oss.internal;


import io.flamingock.oss.core.audit.single.SingleAuditProcessStatus;
import io.flamingock.oss.core.lock.Lock;
import io.flamingock.oss.core.lock.LockAcquirer;
import io.flamingock.oss.core.lock.LockCheckException;
import io.flamingock.oss.core.process.LoadedProcess;
import io.flamingock.oss.core.process.single.SingleExecutableProcess;

public abstract class MongockLockAcquirer implements LockAcquirer<SingleAuditProcessStatus, SingleExecutableProcess> {
    @Override
    public Lock acquireIfRequired(LoadedProcess<SingleAuditProcessStatus, SingleExecutableProcess> loadedProcess) throws LockCheckException {
        throw new RuntimeException("NOT IMPLEMENTED");
    }

    protected abstract void initialize(boolean indexCreation);
}
