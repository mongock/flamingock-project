package io.flamingock.core.core.execution.step;

import io.flamingock.core.core.task.descriptor.TaskDescriptor;
import io.flamingock.core.core.task.executable.ExecutableTask;

public interface TaskStep {
    ExecutableTask getTask();

    default TaskDescriptor getTaskDescriptor() {
        return getTask().getDescriptor();
    }

}
