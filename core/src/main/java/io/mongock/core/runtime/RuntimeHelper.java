package io.mongock.core.runtime;

import java.lang.reflect.Method;

public interface RuntimeHelper {
    Object getInstance(Class<?> type);

    Object executeMethod(Object instance, Method method);

}
