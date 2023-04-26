package io.mongock.core.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;

public final class ReflectionUtil {
    private ReflectionUtil() {}
    public static Optional<Method> findFirstMethodAnnotated(Class<?> source, Class<? extends Annotation> annotation) {
        return Arrays.stream(source.getMethods())
                .filter(method -> method.isAnnotationPresent(annotation))
                .findFirst();
    }

    public static Collection<Class<?>> loadClassesFromPackage(String packagePath) {
        return Collections.emptyList();
    }


}
