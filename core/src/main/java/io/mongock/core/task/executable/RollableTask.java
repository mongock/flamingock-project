package io.mongock.core.task.executable;

import io.mongock.core.runtime.RuntimeManager;

public interface RollableTask extends ExecutableTask {
    void rollback(RuntimeManager runtimeHelper);

    String getRollbackMethodName();
}