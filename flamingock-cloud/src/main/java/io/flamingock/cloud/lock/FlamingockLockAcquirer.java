package io.flamingock.cloud.lock;


import io.flamingock.cloud.process.FlamingockExecutableProcess;
import io.flamingock.cloud.state.FlamingockAuditProcessStatus;
import io.flamingock.core.core.lock.LockAcquirer;
import io.flamingock.core.core.lock.LockAcquisition;
import io.flamingock.core.core.lock.LockException;
import io.flamingock.core.core.lock.LockOptions;
import io.flamingock.core.core.process.LoadedProcess;

public class FlamingockLockAcquirer implements LockAcquirer<FlamingockAuditProcessStatus, FlamingockExecutableProcess> {

    @Override
    public LockAcquisition acquireIfRequired(LoadedProcess<FlamingockAuditProcessStatus, FlamingockExecutableProcess> loadedProcess, LockOptions options) throws LockException {
        return null;
    }
}
