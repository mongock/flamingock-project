package io.flamingock.core.core.task.executable;

import io.flamingock.core.api.annotations.ChangeUnit;
import io.flamingock.core.core.audit.domain.AuditEntryStatus;
import io.flamingock.core.core.runtime.RuntimeManager;
import io.flamingock.core.core.task.Task;
import io.flamingock.core.core.task.descriptor.ReflectionTaskDescriptor;
import io.flamingock.core.core.task.descriptor.TaskDescriptor;
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
        private enum TaskType {CHANGE_UNIT}

        private Builder() {}

        public static List<? extends ExecutableTask> build(TaskDescriptor taskDescriptor, AuditEntryStatus initialState) {
            switch (getType(taskDescriptor)) {
                case CHANGE_UNIT: return ExecutableChangeUnitBuilder.build((ReflectionTaskDescriptor)taskDescriptor, initialState);
                default:
                    throw new IllegalArgumentException(String.format("OrderedExecutableTask type not recognised[%s]", taskDescriptor.getClass().getName()));
            }
        }

        private static Builder.TaskType getType(TaskDescriptor orderedTaskDescriptor) {
            if (orderedTaskDescriptor instanceof ReflectionTaskDescriptor &&
                    ((ReflectionTaskDescriptor)orderedTaskDescriptor).getSource().isAnnotationPresent(ChangeUnit.class)) {
                return Builder.TaskType.CHANGE_UNIT;
            }
            throw new IllegalArgumentException(String.format("OrderedExecutableTask type not recognised[%s]", orderedTaskDescriptor.getClass().getName()));
        }
    }

}