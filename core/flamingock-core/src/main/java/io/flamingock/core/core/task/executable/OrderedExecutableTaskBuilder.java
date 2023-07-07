package io.flamingock.core.core.task.executable;

import io.flamingock.core.api.annotations.ChangeUnit;
import io.flamingock.core.core.audit.domain.AuditEntryStatus;
import io.flamingock.core.core.task.descriptor.OrderedTaskDescriptor;
import io.flamingock.core.core.task.descriptor.impl.ReflectionTaskDescriptor;
import io.flamingock.core.core.task.executable.change.ExecutableChangeUnitBuilder;

import java.util.List;


final class OrderedExecutableTaskBuilder {

    private enum TaskType {CHANGE_UNIT}

    private OrderedExecutableTaskBuilder() {}

    static List<OrderedExecutableTask> build(OrderedTaskDescriptor taskDescriptor, AuditEntryStatus initialState) {
        switch (getType(taskDescriptor)) {
            case CHANGE_UNIT: return ExecutableChangeUnitBuilder.build((ReflectionTaskDescriptor)taskDescriptor, initialState);
            default:
                throw new IllegalArgumentException(String.format("OrderedExecutableTask type not recognised[%s]", taskDescriptor.getClass().getName()));
        }
    }

    private static TaskType getType(OrderedTaskDescriptor orderedTaskDescriptor) {
        if (orderedTaskDescriptor instanceof ReflectionTaskDescriptor &&
                ((ReflectionTaskDescriptor)orderedTaskDescriptor).getSource().isAnnotationPresent(ChangeUnit.class)) {
            return TaskType.CHANGE_UNIT;
        }
        throw new IllegalArgumentException(String.format("OrderedExecutableTask type not recognised[%s]", orderedTaskDescriptor.getClass().getName()));
    }

}
