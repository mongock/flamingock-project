package io.flamingock.core.core;

import io.flamingock.core.core.audit.AuditReader;
import io.flamingock.core.core.audit.domain.AuditStageStatus;
import io.flamingock.core.core.execution.executor.ProcessExecutor;
import io.flamingock.core.core.lock.LockAcquirer;
import io.flamingock.core.core.stage.ExecutableStage;
import io.flamingock.core.core.stage.StageDefinition;
import io.flamingock.core.core.runtime.dependency.DependencyContext;

public interface Factory<
        AUDIT_PROCESS_STATE extends AuditStageStatus,
        EXECUTABLE_PROCESS extends ExecutableStage,
        CONFIGURATION> {

    LockAcquirer<AUDIT_PROCESS_STATE, EXECUTABLE_PROCESS> getLockProvider();

    AuditReader<AUDIT_PROCESS_STATE> getAuditReader();

    StageDefinition<AUDIT_PROCESS_STATE, EXECUTABLE_PROCESS> getDefinitionProcess(CONFIGURATION config);

    ProcessExecutor<EXECUTABLE_PROCESS> getProcessExecutor(DependencyContext dependencyManager);

}
