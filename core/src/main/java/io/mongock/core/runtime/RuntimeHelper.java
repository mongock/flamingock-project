package io.mongock.core.runtime;

import io.mongock.core.lock.Lock;
import io.mongock.core.runtime.dependency.AbstractDependencyManager;
import io.mongock.core.runtime.proxy.LockGuardProxyFactory;

import java.lang.reflect.Method;

public interface RuntimeHelper {
    Object getInstance(Class<?> type);

    Object executeMethod(Object instance, Method method);

    static Builder builder() {
        return new Builder();
    }

    final class Builder {
        
        private AbstractDependencyManager dependencyManager;
        private Lock lock;

        public Builder setLock(Lock lock) {
            this.lock = lock;
            return this;
        }

        public Builder setDependencyManager(AbstractDependencyManager dependencyManager) {
            this.dependencyManager = dependencyManager;
            return this;
        }

        public RuntimeHelper build() {
            LockGuardProxyFactory proxyFactory = new LockGuardProxyFactory(lock);
            return new DefaultRuntimeHelper(proxyFactory, dependencyManager);
        }
    }

}
