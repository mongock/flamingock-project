package io.flamingock.oss.core.task.executable;

import io.flamingock.oss.core.runtime.RuntimeManager;

public interface RollableTask extends ExecutableTask {
    void rollback(RuntimeManager runtimeHelper);

    String getRollbackMethodName();
}