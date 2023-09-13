package io.flamingock.core.task.descriptor;

import java.util.Optional;

public class ReflectionTaskDescriptor extends AbstractTaskDescriptor implements TaskDescriptor {

    private final Class<?> source;

    public ReflectionTaskDescriptor(String id, String order, Class<?> source, boolean runAlways, boolean transactional) {
        super(id, order, runAlways, transactional);
        this.source = source;
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
