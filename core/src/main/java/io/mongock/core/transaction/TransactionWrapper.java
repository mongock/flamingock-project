package io.mongock.core.transaction;

import io.mongock.core.task.descriptor.TaskDescriptor;
import io.mongock.core.execution.step.SuccessableStep;

import java.util.function.Supplier;

public interface TransactionWrapper {


    <T> T wrapInTransaction(TaskDescriptor taskDescriptor, Supplier<T> operation);

}
