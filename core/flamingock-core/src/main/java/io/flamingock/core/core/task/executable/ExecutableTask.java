package io.flamingock.core.core.task.executable;

import io.flamingock.core.api.annotations.ChangeUnit;
import io.flamingock.core.core.audit.domain.AuditEntryStatus;
import io.flamingock.core.core.runtime.RuntimeManager;
import io.flamingock.core.core.task.Task;
import io.flamingock.core.core.task.descriptor.TaskDescriptor;
import io.flamingock.core.core.task.descriptor.reflection.SortedReflectionTaskDescriptor;
import io.flamingock.core.core.task.executable.change.ExecutableChangeUnitBuilder;

import java.util.List;

public interface ExecutableTask extends Task {

    void execute(RuntimeManager runtimeHelper);

    String getExecutionMethodName();

    boolean isInitialExecutionRequired();

    static List<? extends ExecutableTask> build(TaskDescriptor descriptor, AuditEntryStatus auditEntryStatus) {
        return Builder.build(descriptor, auditEntryStatus);
    }

    final class Builder {

        private Builder() {}

        public static List<? extends ExecutableTask> build(TaskDescriptor taskDescriptor, AuditEntryStatus initialState) {
            if (isReflectionChangeUnit(taskDescriptor)) {
                return ExecutableChangeUnitBuilder.build((SortedReflectionTaskDescriptor)taskDescriptor, initialState);
            }
            throw new IllegalArgumentException(String.format("ExecutableTask type not recognised[%s]", taskDescriptor.getClass().getName()));
        }

        private static boolean isReflectionChangeUnit(TaskDescriptor taskDescriptor) {
            return taskDescriptor instanceof SortedReflectionTaskDescriptor &&
                    ((SortedReflectionTaskDescriptor) taskDescriptor).getSource().isAnnotationPresent(ChangeUnit.class);
        }

    }

}