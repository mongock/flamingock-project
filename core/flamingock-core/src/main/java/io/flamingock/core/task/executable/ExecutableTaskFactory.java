package io.flamingock.core.task.executable;

import io.flamingock.core.audit.writer.AuditEntryStatus;
import io.flamingock.core.task.descriptor.TaskDescriptor;

import java.util.List;

public interface ExecutableTaskFactory {

    List<? extends ExecutableTask> extractTasks(TaskDescriptor taskDescriptor, AuditEntryStatus initialState);
}
