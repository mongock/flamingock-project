package io.flamingock.core.core.process;

import io.flamingock.core.core.audit.domain.AuditProcessStatus;
import io.flamingock.core.core.task.filter.TaskFilter;

import java.util.Collection;

/**
 * This class represents the process defined by the user in the builder, yaml, etc.
 * It doesn't necessary contain directly the tasks, it can contain the scanPackage, etc.
 */
public interface DefinitionProcess<AUDIT_PROCESS_STATE extends AuditProcessStatus, EXECUTABLE_PROCESS extends ExecutableProcess> {

    LoadedProcess<AUDIT_PROCESS_STATE, EXECUTABLE_PROCESS> load(Collection<TaskFilter<?>> filters);

}
