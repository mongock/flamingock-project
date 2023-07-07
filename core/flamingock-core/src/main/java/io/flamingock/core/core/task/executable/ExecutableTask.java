package io.flamingock.core.core.task.executable;

import io.flamingock.core.core.runtime.RuntimeManager;
import io.flamingock.core.core.task.Task;
import io.flamingock.core.core.task.descriptor.TaskDescriptor;

public interface ExecutableTask<DESCRIPTOR extends TaskDescriptor> extends Task<DESCRIPTOR> {

    void execute(RuntimeManager runtimeHelper);

    String getExecutionMethodName();

    boolean isInitialExecutionRequired();

}