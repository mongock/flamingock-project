package io.mongock.core.task.executable;

import io.mongock.core.runtime.DefaultRuntimeHelper;

public interface RollableTask extends ExecutableTask {
    void rollback(DefaultRuntimeHelper runtimeHelper);

    String getRollbackMethodName();
}