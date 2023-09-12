package io.flamingock.core.task.descriptor;

import io.flamingock.core.api.annotations.ChangeUnit;
import io.flamingock.core.task.descriptor.AbstractTaskDescriptor;

public class ReflectionTaskDescriptor extends AbstractTaskDescriptor {
    private final Class<?> source;

    public ReflectionTaskDescriptor(String id, Class<?> source, boolean runAlways, boolean transactional) {
        super(id, runAlways, transactional);
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
