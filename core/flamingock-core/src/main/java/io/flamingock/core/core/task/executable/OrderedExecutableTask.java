package io.flamingock.core.core.task.executable;

import io.flamingock.core.core.audit.domain.AuditEntryStatus;
import io.flamingock.core.core.task.descriptor.OrderedTaskDescriptor;

import java.util.List;

public interface OrderedExecutableTask extends ExecutableTask, Comparable<OrderedExecutableTask> {
    String getOrder();

    @Override
    default int compareTo(OrderedExecutableTask other) {
        return this.getOrder().compareTo(other.getOrder());
    }

    static List<OrderedExecutableTask> build(OrderedTaskDescriptor taskDescriptor, AuditEntryStatus initialState) {
        return ExecutableTaskBuilder.build(taskDescriptor, initialState);
    }
}