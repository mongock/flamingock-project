package io.mongock.core.task.executable;

import io.mongock.core.util.RuntimeHelper;

public interface RollableTask extends ExecutableTask {
    void rollback(RuntimeHelper runtimeHelper);

    String getRollbackMethodName();
}