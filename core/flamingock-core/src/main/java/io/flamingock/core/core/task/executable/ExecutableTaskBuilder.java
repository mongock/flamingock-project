package io.flamingock.core.core.task.executable;

import io.flamingock.core.api.annotations.ChangeUnit;
import io.flamingock.core.core.audit.domain.AuditEntryStatus;
import io.flamingock.core.core.task.descriptor.TaskDescriptor;
import io.flamingock.core.core.task.descriptor.ReflectionTaskDescriptor;
import io.flamingock.core.core.task.executable.change.ExecutableChangeUnitBuilder;

import java.util.List;


public final class ExecutableTaskBuilder {

    private enum TaskType {CHANGE_UNIT}

    private ExecutableTaskBuilder() {}

    public static List<? extends ExecutableTask> build(TaskDescriptor taskDescriptor, AuditEntryStatus initialState) {
        switch (getType(taskDescriptor)) {
            case CHANGE_UNIT: return ExecutableChangeUnitBuilder.build((ReflectionTaskDescriptor)taskDescriptor, initialState);
            default:
                throw new IllegalArgumentException(String.format("OrderedExecutableTask type not recognised[%s]", taskDescriptor.getClass().getName()));
        }
    }

    private static TaskType getType(TaskDescriptor orderedTaskDescriptor) {
        if (orderedTaskDescriptor instanceof ReflectionTaskDescriptor &&
                ((ReflectionTaskDescriptor)orderedTaskDescriptor).getSource().isAnnotationPresent(ChangeUnit.class)) {
            return TaskType.CHANGE_UNIT;
        }
        throw new IllegalArgumentException(String.format("OrderedExecutableTask type not recognised[%s]", orderedTaskDescriptor.getClass().getName()));
    }

}
