package io.flamingock.core.core.runtime.dependency;

import java.util.Collection;

public interface DependencyInjector {

    default void addDependencies(Collection<? extends Dependency> dependencies) {
        dependencies.forEach(this::addDependency);
    }

    default void addDependency(Object object) {
        addDependency(new Dependency(object));
    }

    void addDependency(Dependency dependency);
}
