package io.mongock.core.process;

import io.mongock.core.audit.domain.AuditProcessStatus;

/**
 * It's the result of adding the loaded task to the ProcessDefinition
 */
public interface LoadedProcess<AUDIT_PROCESS_STATE extends AuditProcessStatus, EXECUTABLE_PROCESS extends ExecutableProcess> {

    EXECUTABLE_PROCESS applyState(AUDIT_PROCESS_STATE state);

}
