package io.mongock.core.audit.writer;

import io.mongock.core.execution.executor.ExecutionContext;
import io.mongock.core.task.descriptor.TaskDescriptor;

public class AuditItem {


    public enum Operation {EXECUTION, ROLLBACK}

    private final Operation operation;
    private final TaskDescriptor taskDescriptor;
    private final ExecutionContext executionContext;
    private final RuntimeContext runtimeContext;

    public AuditItem(Operation operation,
                     TaskDescriptor taskDescriptor,
                     ExecutionContext executionContext,
                     RuntimeContext runtimeContext) {
        this.operation = operation;
        this.taskDescriptor = taskDescriptor;
        this.executionContext = executionContext;
        this.runtimeContext = runtimeContext;
    }

    public Operation getOperation() {
        return operation;
    }

    public TaskDescriptor getTaskDescriptor() {
        return taskDescriptor;
    }

    public ExecutionContext getExecutionContext() {
        return executionContext;
    }

    public RuntimeContext getRuntimeContext() {
        return runtimeContext;
    }

}