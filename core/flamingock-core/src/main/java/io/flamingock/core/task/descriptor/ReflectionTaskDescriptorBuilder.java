package io.flamingock.core.task.descriptor;

import io.flamingock.core.api.annotations.ChangeUnit;

public class ReflectionTaskDescriptorBuilder {
    private static final ReflectionTaskDescriptorBuilder instance = new ReflectionTaskDescriptorBuilder();

    public static ReflectionTaskDescriptorBuilder recycledBuilder() {
        return instance;
    }

    private Class<?> source;

    private ReflectionTaskDescriptorBuilder() {
    }

    public ReflectionTaskDescriptorBuilder setSource(Class<?> source) {
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

        return new ReflectionTaskDescriptor(
                changeUnitAnnotation.id(),
                changeUnitAnnotation.order(),
                source,
                changeUnitAnnotation.runAlways(),
                changeUnitAnnotation.transactional());
    }
}
