package io.flamingock.core.audit.writer;

import io.flamingock.core.stage.execution.StageExecutionContext;
import io.flamingock.core.task.descriptor.TaskDescriptor;

public class AuditItem {


    public enum Operation {EXECUTION, ROLLBACK}

    private final Operation operation;
    private final TaskDescriptor taskDescriptor;
    private final StageExecutionContext stageExecutionContext;
    private final RuntimeContext runtimeContext;

    public AuditItem(Operation operation,
                     TaskDescriptor taskDescriptor,
                     StageExecutionContext stageExecutionContext,
                     RuntimeContext runtimeContext) {
        this.operation = operation;
        this.taskDescriptor = taskDescriptor;
        this.stageExecutionContext = stageExecutionContext;
        this.runtimeContext = runtimeContext;
    }

    public Operation getOperation() {
        return operation;
    }

    public TaskDescriptor getTaskDescriptor() {
        return taskDescriptor;
    }

    public StageExecutionContext getExecutionContext() {
        return stageExecutionContext;
    }

    public RuntimeContext getRuntimeContext() {
        return runtimeContext;
    }

}