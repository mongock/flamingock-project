package io.flamingock.core.core.task.executable;

import io.flamingock.core.core.runtime.RuntimeManager;

public interface Rollback {

    ExecutableTask getTask();

    void rollback(RuntimeManager runtimeHelper);

    String getRollbackMethodName();
}