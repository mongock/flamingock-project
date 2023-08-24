package io.flamingock.core.runtime.proxy;

public interface GuardProxyFactory {
    <T> T getProxy(T targetObject, Class<? super T> interfaceType);

    Object getRawProxy(Object targetObject, Class<?> interfaceType);
}
