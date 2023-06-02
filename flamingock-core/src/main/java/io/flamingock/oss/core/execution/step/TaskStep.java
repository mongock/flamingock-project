package io.flamingock.oss.core.execution.step;

import io.flamingock.oss.core.task.descriptor.TaskDescriptor;
import io.flamingock.oss.core.task.executable.ExecutableTask;

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
