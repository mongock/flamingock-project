package io.flamingock.core.task.descriptor;

public class SortedReflectionTaskDescriptor extends ReflectionTaskDescriptor implements SortedTaskDescriptor {

    private final String order;

    public SortedReflectionTaskDescriptor(String id, String order, Class<?> source, boolean runAlways, boolean transactional) {
        super(id, source, runAlways, transactional);
        this.order = order;
    }

    @Override
    public String getOrder() {
        return order;
    }
}