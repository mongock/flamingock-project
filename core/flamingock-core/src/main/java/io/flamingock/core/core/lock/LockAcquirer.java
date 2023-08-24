package io.flamingock.core.core.lock;

import io.flamingock.core.core.audit.domain.AuditStageStatus;
import io.flamingock.core.core.stage.ExecutableStage;
import io.flamingock.core.core.stage.LoadedStage;

public interface LockAcquirer<AUDIT_PROCESS_STATE extends AuditStageStatus, EXECUTABLE_PROCESS extends ExecutableStage> {

    default LockAcquisition acquireIfRequired(LoadedStage<AUDIT_PROCESS_STATE, EXECUTABLE_PROCESS> loadedStage) throws LockException {
        return acquireIfRequired(loadedStage, LockOptions.builder().build());
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
     * @param loadedStage
     * @param options
     * @return
     * @throws LockException
     */
    LockAcquisition acquireIfRequired(LoadedStage<AUDIT_PROCESS_STATE, EXECUTABLE_PROCESS> loadedStage,
                                      LockOptions options) throws LockException;
}
