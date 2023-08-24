package io.flamingock.core.core.stage;

import io.flamingock.core.core.audit.domain.AuditStageStatus;

/**
 * This class represents the process defined by the user in the builder, yaml, etc.
 * It doesn't necessary contain directly the tasks, it can contain the scanPackage, etc.
 */
public interface StageDefinition<AUDIT_PROCESS_STATE extends AuditStageStatus, EXECUTABLE_PROCESS extends ExecutableStage> {

    /**
     * It loads the definition from the source(scanPackage, yaml definition, etc.) and returns the LoadedStage
     * with contain the task Definition.
     * <br />
     * This method can decide up to some level, which type of process is loaded. For example in the case of 'SingleStageDefinition',
     * depending on the tasks inside the package or some field in the yaml, it returns a SingleLoadedStage or ParallelSingleLoadedProcess.
     * <br />
     * @return the LoadedStage with contain the task Definition
     */
    LoadedStage<AUDIT_PROCESS_STATE, EXECUTABLE_PROCESS> load();

}
