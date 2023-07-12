package io.flamingock.core.core.task.descriptor.reflection;

import io.flamingock.core.core.task.descriptor.OrderedTaskDescriptor;

public class OrderedReflectionTaskDescriptor extends ReflectionTaskDescriptor implements OrderedTaskDescriptor {

    private final String order;

    public OrderedReflectionTaskDescriptor(String id, String order, Class<?> source, boolean runAlways) {
        super(id, source, runAlways);
        this.order = order;
    }

    @Override
    public String getOrder() {
        return order;
    }
}
