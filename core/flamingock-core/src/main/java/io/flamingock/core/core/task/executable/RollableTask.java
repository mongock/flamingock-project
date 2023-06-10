package io.flamingock.core.core.task.executable;

import io.flamingock.core.core.runtime.RuntimeManager;

public interface RollableTask extends ExecutableTask {
    void rollback(RuntimeManager runtimeHelper);

    String getRollbackMethodName();
}