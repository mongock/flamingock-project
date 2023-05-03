package io.flamingock.internal.lock;


import io.flamingock.internal.process.FlamingockExecutableProcess;
import io.flamingock.internal.state.FlamingockAuditProcessStatus;
import io.mongock.core.lock.Lock;
import io.mongock.core.lock.LockCheckException;
import io.mongock.core.lock.LockAcquirer;
import io.mongock.core.process.LoadedProcess;

public class FlamingockLockAcquirer implements LockAcquirer<FlamingockAuditProcessStatus, FlamingockExecutableProcess> {

    @Override
    public Lock acquireIfRequired(LoadedProcess<FlamingockAuditProcessStatus, FlamingockExecutableProcess> loadedProcess) throws LockCheckException {
        return null;
    }
}
