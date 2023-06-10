package io.flamingock.core.core.lock;

import io.flamingock.core.core.audit.domain.AuditProcessStatus;
import io.flamingock.core.core.process.ExecutableProcess;
import io.flamingock.core.core.process.LoadedProcess;

public interface LockAcquirer<AUDIT_PROCESS_STATE extends AuditProcessStatus, EXECUTABLE_PROCESS extends ExecutableProcess> {

    default LockAcquisition acquireIfRequired(LoadedProcess<AUDIT_PROCESS_STATE, EXECUTABLE_PROCESS> loadedProcess) throws LockException {
        return acquireIfRequired(loadedProcess, LockOptions.builder().build());
    }

    /**
     * Acquire the lock if available and if there is any outstanding work, based on the stage description passed as
     * parameter.
     * It's intended to be the first acquisition. Once taken(if required), it just needs to be extended(ensured).
     * In case there is pending work to do, but the lock is hold by other process, it blocks until it's acquired
     * or until the retry policy is exceeded.
     *
     * @param loadedProcess stage description from the filesystem
     * @return LockAcquisition.Acquired if required and acquired successfully, or LockAcquisition.NotRequired not required
     * @throws LockException if the lock is required and cannot be acquired within the configured margin(retry, etc.)
     */
    /**
     *
     * @param loadedProcess
     * @param options
     * @return
     * @throws LockException
     */
    LockAcquisition acquireIfRequired(LoadedProcess<AUDIT_PROCESS_STATE, EXECUTABLE_PROCESS> loadedProcess,
                                      LockOptions options) throws LockException;
}
