package io.flamingock.core.task.executable;

import io.flamingock.core.runtime.RuntimeManager;

public interface Rollback {

    ExecutableTask getTask();

    void rollback(RuntimeManager runtimeHelper);

    String getRollbackMethodName();
}