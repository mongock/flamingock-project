package io.mongock.core.lock;

import io.mongock.core.audit.domain.AuditProcessStatus;
import io.mongock.core.process.ExecutableProcess;
import io.mongock.core.process.LoadedProcess;

public interface LockProvider<AUDIT_PROCESS_STATE extends AuditProcessStatus, EXECUTABLE_PROCESS extends ExecutableProcess> {

    /**
     * Acquire the lock if available and if there is any outstanding work, based on the stage description passed as
     * parameter.
     * It's intended to be the first acquisition. Once taken(if required), it just needs to be extended(ensured).
     * In case there is pending work to do, but the lock is hold by other process, it blocks until it's acquired
     * or until the retry policy is exceeded.
     *
     * @param loadedProcess stage description from the filesystem
     * @return acquired lock if it was successfully acquired or false if there is no pending work to do.
     */
    Lock acquireIfRequired(LoadedProcess<AUDIT_PROCESS_STATE, EXECUTABLE_PROCESS> loadedProcess) throws LockCheckException;

}
