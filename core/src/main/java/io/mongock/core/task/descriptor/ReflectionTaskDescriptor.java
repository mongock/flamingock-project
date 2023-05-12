package io.mongock.core.task.descriptor;

public class ReflectionTaskDescriptor extends AbstractTaskDescriptor {

    private final Class<?> source;

    public ReflectionTaskDescriptor(String id,
                                    String order,
                                    Class<?> source, boolean runAlways) {
        super(id, order, runAlways);
        this.source = source;
    }

    public Class<?> getSource() {
        return source;
    }


    @Override
    public String getClassImplementor() {
        return source.getName();
    }

    public static Builder builder() {
        return new Builder();
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
                throw new IllegalArgumentException(String.format("ExecutableTask type not recognised in class[%s]", source.getName()));
            }
        }

        private static boolean isChangeUnit(Class<?> source) {
            return source.isAnnotationPresent((io.mongock.api.annotations.ChangeUnit.class));
        }

        private static ReflectionTaskDescriptor getDescriptorFromChangeUnit(Class<?> source) {
            io.mongock.api.annotations.ChangeUnit changeUnitAnnotation = source.getAnnotation(io.mongock.api.annotations.ChangeUnit.class);

            return new ReflectionTaskDescriptor(
                    changeUnitAnnotation.id(),
                    changeUnitAnnotation.order(),
                    source,
                    changeUnitAnnotation.runAlways());
        }
    }
}
