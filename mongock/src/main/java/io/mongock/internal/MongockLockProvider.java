package io.mongock.internal;


import io.mongock.core.audit.single.SingleAuditProcessStatus;
import io.mongock.core.lock.Lock;
import io.mongock.core.lock.LockCheckException;
import io.mongock.core.lock.LockProvider;
import io.mongock.core.process.LoadedProcess;
import io.mongock.core.process.single.SingleExecutableProcess;

public abstract class MongockLockProvider implements LockProvider<SingleAuditProcessStatus, SingleExecutableProcess> {
    @Override
    public Lock acquireIfRequired(LoadedProcess<SingleAuditProcessStatus, SingleExecutableProcess> loadedProcess) throws LockCheckException {
        throw new RuntimeException("NOT IMPLEMENTED");
    }

    protected abstract void initialize(boolean indexCreation);
}
