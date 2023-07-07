package io.flamingock.core.core.task.executable;

import io.flamingock.core.core.runtime.RuntimeManager;
import io.flamingock.core.core.task.Task;

public interface ExecutableTask extends Task {

    void execute(RuntimeManager runtimeHelper);

    String getExecutionMethodName();

    boolean isInitialExecutionRequired();

    static ExecutableTaskBuilder builder() {
        return new ExecutableTaskBuilder();
    }


}