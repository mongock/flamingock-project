package io.flamingock.core;

import io.flamingock.core.audit.single.SingleAuditReader;
import io.flamingock.core.stage.execution.SequentialStageExecutor;
import io.flamingock.core.lock.LockAcquirer;
import io.flamingock.core.stage.StageDefinition;
import io.flamingock.core.runtime.dependency.DependencyContext;

public interface Factory<CONFIGURATION> {

    LockAcquirer getLockAcquirer();

    SingleAuditReader getAuditReader();

    StageDefinition getDefinitionProcess(CONFIGURATION config);

    SequentialStageExecutor getProcessExecutor(DependencyContext dependencyManager);

}
