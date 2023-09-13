package io.flamingock.core.task.executable.factory;

import io.flamingock.core.audit.domain.AuditEntryStatus;
import io.flamingock.core.task.descriptor.TaskDescriptor;
import io.flamingock.core.task.executable.ExecutableTask;

import java.util.List;

public interface ExecutableTaskFactory {

    List<? extends ExecutableTask> extractTasks(TaskDescriptor taskDescriptor, AuditEntryStatus initialState);
}
