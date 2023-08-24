package io.flamingock.core;

import io.flamingock.core.audit.domain.AuditStageStatus;
import io.flamingock.core.audit.single.SingleAuditReader;
import io.flamingock.core.stage.executor.SequentialStageExecutor;
import io.flamingock.core.lock.LockAcquirer;
import io.flamingock.core.stage.ExecutableStage;
import io.flamingock.core.stage.StageDefinition;
import io.flamingock.core.runtime.dependency.DependencyContext;

public interface Factory<
        AUDIT_PROCESS_STATE extends AuditStageStatus,
        EXECUTABLE_PROCESS extends ExecutableStage,
        CONFIGURATION> {

    LockAcquirer<AUDIT_PROCESS_STATE, EXECUTABLE_PROCESS> getLockProvider();

    SingleAuditReader getAuditReader();

    StageDefinition getDefinitionProcess(CONFIGURATION config);

    SequentialStageExecutor getProcessExecutor(DependencyContext dependencyManager);

}
