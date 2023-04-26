package io.mongock.core.execution.step;

import io.mongock.core.execution.step.execution.ExecutionStep;
import io.mongock.core.execution.step.execution.FailedExecutionStep;
import io.mongock.core.execution.step.execution.SuccessExecutionStep;
import io.mongock.core.task.descriptor.TaskDescriptor;
import io.mongock.core.task.executable.ExecutableTask;
import io.mongock.core.util.RuntimeHelper;
import io.mongock.core.util.StopWatch;

public abstract class TaskStep {

    protected final ExecutableTask task;

    protected TaskStep(ExecutableTask task) {
        this.task = task;
    }


    public ExecutableTask getTask() {
        return task;
    }

    public TaskDescriptor getTaskDescriptor() {
        return task.getDescriptor();
    }

}
