package io.flamingock.core.core.execution.step;

import io.flamingock.core.core.task.descriptor.OrderedTaskDescriptor;
import io.flamingock.core.core.task.descriptor.TaskDescriptor;
import io.flamingock.core.core.task.executable.ExecutableTask;
import io.flamingock.core.core.task.executable.OrderedExecutableTask;

public interface TaskStep {
    ExecutableTask getTask();

    default TaskDescriptor getTaskDescriptor() {
        return getTask().getDescriptor();
    }

}
