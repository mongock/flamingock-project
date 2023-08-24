package io.flamingock.core.transaction;

import io.flamingock.core.runtime.dependency.DependencyInjectable;
import io.flamingock.core.task.descriptor.TaskDescriptor;

import java.util.function.Supplier;

public interface TransactionWrapper {

    <T> T wrapInTransaction(TaskDescriptor taskDescriptor, DependencyInjectable dependencyInjectable, Supplier<T> operation);

}
