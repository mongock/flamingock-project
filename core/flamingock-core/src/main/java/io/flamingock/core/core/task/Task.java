package io.flamingock.core.core.task;

import io.flamingock.core.core.task.descriptor.OrderedTaskDescriptor;
import io.flamingock.core.core.task.descriptor.TaskDescriptor;


/**
 * Top level interface for task that can be executable, for read access like "cli print history" etc.
 */
public interface Task {
    TaskDescriptor getDescriptor();

}