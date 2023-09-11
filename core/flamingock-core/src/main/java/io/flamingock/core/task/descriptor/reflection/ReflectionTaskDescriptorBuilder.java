package io.flamingock.core.task.descriptor.reflection;

import io.flamingock.core.api.annotations.ChangeUnit;

public class ReflectionTaskDescriptorBuilder {
    private static final ReflectionTaskDescriptorBuilder BUILDER = new ReflectionTaskDescriptorBuilder();

    public static ReflectionTaskDescriptorBuilder recycledBuilder() {
        return BUILDER;
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

        return new SortedReflectionTaskDescriptor(
                changeUnitAnnotation.id(),
                changeUnitAnnotation.order(),
                source,
                changeUnitAnnotation.runAlways(),
                changeUnitAnnotation.transactional());
    }
}
