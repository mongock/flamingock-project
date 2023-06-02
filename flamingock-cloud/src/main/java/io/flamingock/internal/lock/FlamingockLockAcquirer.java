package io.flamingock.internal.lock;


import io.flamingock.internal.process.FlamingockExecutableProcess;
import io.flamingock.internal.state.FlamingockAuditProcessStatus;
import io.flamingock.oss.core.lock.Lock;
import io.flamingock.oss.core.lock.LockAcquirer;
import io.flamingock.oss.core.lock.LockCheckException;
import io.flamingock.oss.core.process.LoadedProcess;

public class FlamingockLockAcquirer implements LockAcquirer<FlamingockAuditProcessStatus, FlamingockExecutableProcess> {

    @Override
    public Lock acquireIfRequired(LoadedProcess<FlamingockAuditProcessStatus, FlamingockExecutableProcess> loadedProcess) throws LockCheckException {
        return null;
    }
}
