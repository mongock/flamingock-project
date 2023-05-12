package io.mongock.core.task.executable;

import io.mongock.core.runtime.RuntimeOrchestrator;

public interface RollableTask extends ExecutableTask {
    void rollback(RuntimeOrchestrator runtimeHelper);

    String getRollbackMethodName();
}