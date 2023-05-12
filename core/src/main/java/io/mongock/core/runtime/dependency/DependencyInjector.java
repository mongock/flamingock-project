package io.mongock.core.runtime.dependency;

import java.util.Collection;

public interface DependencyInjector {

    void addDependencies(Collection<? extends Dependency> dependencies);

    void addDependency(Dependency dependency);

    default void addDependency(Object object) {
        addDependency(new Dependency(object));
    }
}
