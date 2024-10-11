package io.flamingock.core.pipeline;

import io.flamingock.commons.utils.ReflectionUtil;
import io.flamingock.core.api.annotations.ChangeUnit;

import java.util.Collection;

public final class ExecutionUtils {

    private static final Class<ChangeUnit> CHANGE_UNIT_CLASS = ChangeUnit.class;

    private static final Class<io.mongock.api.annotations.ChangeUnit> LEGACY_CHANGE_UNIT_CLASS = io.mongock.api.annotations.ChangeUnit.class;

    private ExecutionUtils() {
    }

    @SuppressWarnings("unchecked")
    public static Collection<Class<?>> loadExecutionClassesFromPackage(String packagePath) {
        return ReflectionUtil.loadAnnotatedClassesFromPackage(packagePath, ExecutionUtils.CHANGE_UNIT_CLASS, ExecutionUtils.LEGACY_CHANGE_UNIT_CLASS);
    }


    public static boolean isExecutionClass(Class<?> clazz) {
        return clazz.isAnnotationPresent(CHANGE_UNIT_CLASS) || clazz.isAnnotationPresent(LEGACY_CHANGE_UNIT_CLASS);
    }

}
