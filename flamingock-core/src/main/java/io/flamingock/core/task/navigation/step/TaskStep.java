package io.flamingock.core.task.navigation.step;

import io.flamingock.core.task.descriptor.TaskDescriptor;
import io.flamingock.core.task.executable.ExecutableTask;

public interface TaskStep {
    ExecutableTask getTask();

    default TaskDescriptor getTaskDescriptor() {
        return getTask().getDescriptor();
    }

}
