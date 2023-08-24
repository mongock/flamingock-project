package io.flamingock.core.task.executable;

import io.flamingock.core.task.descriptor.TaskDescriptor;

import java.util.List;

public interface ExecutableTaskFactory {

    boolean matchesDescriptor(TaskDescriptor descriptor);
    List<? extends ExecutableTask> getTasks(TaskDescriptor taskDescriptor);
}
