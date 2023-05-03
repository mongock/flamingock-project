package io.mongock.core.execution.step;

import io.mongock.core.task.descriptor.TaskDescriptor;
import io.mongock.core.task.executable.ExecutableTask;

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
