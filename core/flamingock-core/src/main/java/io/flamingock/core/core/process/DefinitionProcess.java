package io.flamingock.core.core.process;

import io.flamingock.core.core.audit.domain.AuditProcessStatus;
import io.flamingock.core.core.task.filter.TaskFilter;

import java.util.Collection;

/**
 * This class represents the process defined by the user in the builder, yaml, etc.
 * It doesn't necessary contain directly the tasks, it can contain the scanPackage, etc.
 */
public interface DefinitionProcess<AUDIT_PROCESS_STATE extends AuditProcessStatus, EXECUTABLE_PROCESS extends ExecutableProcess> {

    /**
     * It loads the definition from the source(scanPackage, yaml definition, etc.) and returns the LoadedProcess
     * with contain the task Definition.
     * <br />
     * This method can decide up to some level, which type of process is loaded. For example in the case of 'SingleDefinitionProcess',
     * depending on the tasks inside the package or some field in the yaml, it returns a SeqSingleLoadedProcess or ParallelSingleLoadedProcess.
     * <br />
     * @return the LoadedProcess with contain the task Definition
     */
    LoadedProcess<AUDIT_PROCESS_STATE, EXECUTABLE_PROCESS> load();

}
