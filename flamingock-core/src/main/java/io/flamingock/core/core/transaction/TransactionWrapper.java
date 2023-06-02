package io.flamingock.core.core.transaction;

import io.flamingock.core.core.runtime.dependency.DependencyInjector;
import io.flamingock.core.core.task.descriptor.TaskDescriptor;

import java.util.function.Supplier;

public interface TransactionWrapper {


    <T> T wrapInTransaction(TaskDescriptor taskDescriptor, DependencyInjector dependencyInjector, Supplier<T> operation);

}
