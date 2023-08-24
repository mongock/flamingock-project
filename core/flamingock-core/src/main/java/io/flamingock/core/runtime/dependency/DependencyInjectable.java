package io.flamingock.core.runtime.dependency;

import java.util.Collection;

public interface DependencyInjectable {

    default void addDependencies(Collection<? extends Dependency> dependencies) {
        dependencies.forEach(this::addDependency);
    }

    default void addDependency(Object object) {
        addDependency(new Dependency(object));
    }

    void addDependency(Dependency dependency);
}
