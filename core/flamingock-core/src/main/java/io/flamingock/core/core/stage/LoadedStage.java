package io.flamingock.core.core.stage;

import io.flamingock.core.core.audit.domain.AuditStageStatus;

/**
 * It's the result of adding the loaded task to the ProcessDefinition
 */
public interface LoadedStage<AUDIT_PROCESS_STATE extends AuditStageStatus, EXECUTABLE_PROCESS extends ExecutableStage> {

    EXECUTABLE_PROCESS applyState(AUDIT_PROCESS_STATE state);

}
