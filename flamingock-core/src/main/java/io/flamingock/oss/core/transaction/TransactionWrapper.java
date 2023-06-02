package io.flamingock.oss.core.transaction;

import io.flamingock.oss.core.task.descriptor.TaskDescriptor;
import io.flamingock.oss.core.runtime.dependency.DependencyInjector;

import java.util.function.Supplier;

public interface TransactionWrapper {


    <T> T wrapInTransaction(TaskDescriptor taskDescriptor, DependencyInjector dependencyInjector, Supplier<T> operation);

}
