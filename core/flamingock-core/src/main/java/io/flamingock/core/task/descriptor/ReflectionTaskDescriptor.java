package io.flamingock.core.task.descriptor;

public class ReflectionTaskDescriptor extends AbstractTaskDescriptor implements TaskDescriptor {

    private final Class<?> source;
    private final String order;

    public ReflectionTaskDescriptor(String id, String order, Class<?> source, boolean runAlways, boolean transactional) {
        super(id, runAlways, transactional);
        this.source = source;
        this.order = order;
    }

    @Override
    public String getOrder() {
        return order;
    }


    public Class<?> getSource() {
        return source;
    }

    @Override
    public String getClassImplementor() {
        return source.getName();
    }

    @Override
    public String pretty() {
        return toString();
    }
}
