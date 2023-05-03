package io.mongock.core.task.executable;

import io.mongock.core.runtime.DefaultRuntimeHelper;
import io.mongock.core.runtime.RuntimeHelper;

public interface RollableTask extends ExecutableTask {
    void rollback(RuntimeHelper runtimeHelper);

    String getRollbackMethodName();
}