package io.flamingock.core.core.runtime.proxy;

import io.flamingock.core.core.lock.Lock;
import javassist.util.proxy.MethodHandler;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Set;

public class LockGuardMethodHandler<T> implements MethodHandler {

    private final LockGuardProxy<T> lockGuardProxy;

    public LockGuardMethodHandler(T implementation, Lock lockEnsurer, LockGuardProxyFactory proxyFactory, Set<String> nonGuardedMethods) {
        this.lockGuardProxy = new LockGuardProxy<>(implementation, lockEnsurer, proxyFactory, nonGuardedMethods);
    }

    public LockGuardProxy<T> getLockGuardProxy() {
        return lockGuardProxy;
    }

    @Override
    public Object invoke(Object proxy, Method method, Method method1, Object[] methodArgs) throws Throwable {
        try {
            return lockGuardProxy.invoke(proxy, method, methodArgs);
        } catch (InvocationTargetException ex) {
            if (ex.getTargetException() != null) {
                throw ex.getTargetException();
            }
            throw ex;
        }
    }
}
