package io.mongock.core.transaction;

import io.mongock.core.task.descriptor.TaskDescriptor;
import io.mongock.core.util.Result;

import java.util.function.Supplier;

public interface TransactionWrapper {

    Result wrapInTransaction(TaskDescriptor taskDescriptor, Runnable operation);

}
