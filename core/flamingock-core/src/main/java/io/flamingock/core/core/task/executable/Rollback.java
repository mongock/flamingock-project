package io.flamingock.core.core.task.executable;

import io.flamingock.core.core.runtime.RuntimeManager;
import io.flamingock.core.core.task.executable.ExecutableTask;

public interface Rollback {

    ExecutableTask getTask();
    void rollback(RuntimeManager runtimeHelper);

    String getRollbackMethodName();
}