package io.mongock.core.transaction;

import io.mongock.core.runtime.dependency.AbstractDependencyManager;
import io.mongock.core.runtime.dependency.DependencyInjector;
import io.mongock.core.task.descriptor.TaskDescriptor;
import io.mongock.core.execution.step.SuccessableStep;

import java.util.function.Supplier;

public interface TransactionWrapper {


    <T> T wrapInTransaction(TaskDescriptor taskDescriptor, DependencyInjector dependencyInjector, Supplier<T> operation);

}
