package io.flamingock.core.core.execution.step;

import io.flamingock.core.core.task.descriptor.OrderedTaskDescriptor;
import io.flamingock.core.core.task.executable.ExecutableTask;

public interface TaskStep {
    ExecutableTask getTask();

    default OrderedTaskDescriptor getTaskDescriptor() {
        return getTask().getDescriptor();
    }

}
