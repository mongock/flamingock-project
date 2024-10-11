package io.flamingock.core.utils;

import io.flamingock.commons.utils.ReflectionUtil;
import io.flamingock.core.api.annotations.ChangeUnit;
import io.flamingock.core.api.annotations.Execution;
import io.flamingock.core.task.descriptor.ReflectionTaskDescriptor;

import java.lang.reflect.Method;
import java.util.Collection;

public final class ExecutionUtils {

    private static final Class<ChangeUnit> CHANGE_UNIT_CLASS = ChangeUnit.class;

    private static final Class<io.mongock.api.annotations.ChangeUnit> LEGACY_CHANGE_UNIT_CLASS = io.mongock.api.annotations.ChangeUnit.class;

    private static final Class<Execution> EXECUTION_CLASS = Execution.class;

    private static final Class<io.mongock.api.annotations.Execution> LEGACY_EXECUTION_CLASS = io.mongock.api.annotations.Execution.class;

    private ExecutionUtils() {
    }

    @SuppressWarnings("unchecked")
    public static Collection<Class<?>> loadExecutionClassesFromPackage(String packagePath) {
        return ReflectionUtil.loadAnnotatedClassesFromPackage(packagePath, ExecutionUtils.CHANGE_UNIT_CLASS, ExecutionUtils.LEGACY_CHANGE_UNIT_CLASS);
    }

    /**
     * We need this generic method, although currently it only calls `isChangeUnit`, but in the future there will
     * more annotations, so `isChangeUnit` will be only one of them.
     */
    public static boolean isExecutableClass(Class<?> clazz) {
        return isChangeUnit(clazz);
    }

    public static boolean isChangeUnit(Class<?> clazz) {
        return clazz.isAnnotationPresent(CHANGE_UNIT_CLASS) || clazz.isAnnotationPresent(LEGACY_CHANGE_UNIT_CLASS);
    }

    public static boolean isNewChangeUnit(Class<?> clazz) {
        return clazz.isAnnotationPresent(CHANGE_UNIT_CLASS);
    }

    public static boolean isLegacyChangeUnit(Class<?> clazz) {
        return clazz.isAnnotationPresent(LEGACY_CHANGE_UNIT_CLASS);
    }

    @SuppressWarnings("unchecked")
    public static Method getExecutionMethodOrThrow(Class<?> sourceClass) {
        return ReflectionUtil.findFirstAnnotatedMethod(sourceClass, EXECUTION_CLASS, LEGACY_EXECUTION_CLASS)
                .orElseThrow(() -> new IllegalArgumentException(String.format(
                        "ExecutableChangeUnit[%s] without %s method",
                        sourceClass.getName(),
                        Execution.class.getSimpleName())));
    }

}
