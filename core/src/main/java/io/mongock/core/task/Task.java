package io.mongock.core.task;

import io.mongock.core.task.descriptor.TaskDescriptor;


/**
 * Top level interface for task that can be executable, for read access like "cli print history" etc.
 */
public interface Task {
    TaskDescriptor getDescriptor();

}