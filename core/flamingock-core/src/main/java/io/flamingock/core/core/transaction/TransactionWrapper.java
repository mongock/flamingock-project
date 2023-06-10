package io.flamingock.core.core.transaction;

import io.flamingock.core.core.runtime.dependency.DependencyInjectable;
import io.flamingock.core.core.task.descriptor.TaskDescriptor;

import java.util.function.Supplier;

public interface TransactionWrapper {


    <T> T wrapInTransaction(TaskDescriptor taskDescriptor, DependencyInjectable dependencyInjectable, Supplier<T> operation);

}
