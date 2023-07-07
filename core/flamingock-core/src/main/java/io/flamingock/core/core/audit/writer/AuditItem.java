package io.flamingock.core.core.audit.writer;

import io.flamingock.core.core.execution.executor.ExecutionContext;
import io.flamingock.core.core.task.descriptor.OrderedTaskDescriptor;

public class AuditItem {


    public enum Operation {EXECUTION, ROLLBACK}

    private final Operation operation;
    private final OrderedTaskDescriptor taskDescriptor;
    private final ExecutionContext executionContext;
    private final RuntimeContext runtimeContext;

    public AuditItem(Operation operation,
                     OrderedTaskDescriptor taskDescriptor,
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

    public OrderedTaskDescriptor getTaskDescriptor() {
        return taskDescriptor;
    }

    public ExecutionContext getExecutionContext() {
        return executionContext;
    }

    public RuntimeContext getRuntimeContext() {
        return runtimeContext;
    }

}