package io.flamingock.core.task.descriptor;

public class ReflectionTaskDescriptor<SOURCE_CLASS> extends AbstractTaskDescriptor implements TaskDescriptor {

    protected final Class<SOURCE_CLASS> source;

    public ReflectionTaskDescriptor(String id, String order, Class<SOURCE_CLASS> source, boolean runAlways, boolean transactional) {
        super(id, order, runAlways, transactional);
        this.source = source;
    }

    public Class<SOURCE_CLASS> getSourceClass() {
        return source;
    }

    @Override
    public String getSourceName() {
        return source.getName();
    }

    @Override
    public String pretty() {
        return toString();
    }
}
