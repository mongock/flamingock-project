package io.flamingock.core.core.task.executable;

import io.flamingock.core.core.task.descriptor.TaskDescriptor;

import java.util.List;

public interface ExecutableTaskFactory {

    boolean matchesDescriptor(TaskDescriptor descriptor);
    List<? extends ExecutableTask> getTasks(TaskDescriptor taskDescriptor);
}
