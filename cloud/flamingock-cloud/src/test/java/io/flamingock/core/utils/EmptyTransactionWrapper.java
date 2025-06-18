package io.flamingock.core.utils;

import io.flamingock.internal.commons.core.context.DependencyInjectable;
import io.flamingock.internal.commons.core.task.TaskDescriptor;
import io.flamingock.internal.core.transaction.TransactionWrapper;

import java.util.function.Supplier;

public class EmptyTransactionWrapper implements TransactionWrapper {

    private boolean called = false;
    @Override
    public <T> T wrapInTransaction(TaskDescriptor loadedTask, DependencyInjectable dependencyInjectable, Supplier<T> operation) {
        called = true;
        return operation.get();
    }

    public boolean isCalled() {
        return called;
    }


}
