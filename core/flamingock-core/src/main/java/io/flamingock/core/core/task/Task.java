package io.flamingock.core.core.task;

import io.flamingock.core.core.task.descriptor.TaskDescriptor;


/**
 * Top level interface for task that can be executable, for read access like "cli print history" etc.
 */
public interface Task extends Comparable<Task> {
    TaskDescriptor getDescriptor();

    @Override
    default int compareTo(Task other) {
        return this.getDescriptor().compareTo(other.getDescriptor());

    }

}