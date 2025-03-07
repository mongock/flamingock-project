package io.flamingock.core.engine.audit.domain;

import io.flamingock.core.pipeline.execution.ExecutionContext;
import io.flamingock.core.task.descriptor.LoadedTask;

public class ExecutionAuditItem extends AuditItem{

    public ExecutionAuditItem(LoadedTask loadedTask, ExecutionContext executionContext, RuntimeContext runtimeContext) {
        super(Operation.EXECUTION, loadedTask, executionContext, runtimeContext);
    }
}
