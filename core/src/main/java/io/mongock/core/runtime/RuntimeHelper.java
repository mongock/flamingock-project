package io.mongock.core.runtime;

import io.mongock.core.lock.Lock;
import io.mongock.core.runtime.dependency.DependencyManager;
import io.mongock.core.runtime.proxy.LockGuardProxyFactory;

import java.lang.reflect.Method;

public interface RuntimeHelper {
    Object getInstance(Class<?> type);

    Object executeMethod(Object instance, Method method);

    final class Builder {
        
        private final DependencyManager dependencyManager;
        private Lock lock;

        public Builder(DependencyManager dependencyManager) {
            this.dependencyManager = dependencyManager;
        }

        public Builder setLock(Lock lock) {
            this.lock = lock;
            return this;
        }

        public RuntimeHelper build() {
            LockGuardProxyFactory proxyFactory = new LockGuardProxyFactory(lock);
            return new DefaultRuntimeHelper(proxyFactory, dependencyManager);
        }
    }

}
