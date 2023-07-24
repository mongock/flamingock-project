package io.flamingock.core.core.task.descriptor.reflection;

import io.flamingock.core.api.annotations.ChangeUnit;
import io.flamingock.core.core.task.descriptor.AbstractTaskDescriptor;

public class ReflectionTaskDescriptor extends AbstractTaskDescriptor {

    private static final Builder BUILDER = new Builder();
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


    public static Builder recycledBuilder() {
        return BUILDER;
    }


    public static final class Builder {

        private Class<?> source;

        private Builder() {
        }

        public Builder setSource(Class<?> source) {
            this.source = source;
            return this;
        }


        public ReflectionTaskDescriptor build() {
            if (isChangeUnit(source)) {
                return getDescriptorFromChangeUnit(source);
            } else {
                throw new IllegalArgumentException(String.format("Task type not recognised in class[%s]", source.getName()));
            }
        }

        private static boolean isChangeUnit(Class<?> source) {
            return source.isAnnotationPresent((ChangeUnit.class));
        }

        private static ReflectionTaskDescriptor getDescriptorFromChangeUnit(Class<?> source) {
            ChangeUnit changeUnitAnnotation = source.getAnnotation(ChangeUnit.class);

            return new SortedReflectionTaskDescriptor(
                    changeUnitAnnotation.id(),
                    changeUnitAnnotation.order(),
                    source,
                    changeUnitAnnotation.runAlways(),
                    changeUnitAnnotation.transactional());
        }
    }
}
