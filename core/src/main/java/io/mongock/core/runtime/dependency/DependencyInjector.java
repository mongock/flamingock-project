package io.mongock.core.runtime.dependency;

import java.util.Collection;

public interface DependencyInjector {

    void addPriorityDependencies(Collection<? extends Dependency> dependencies);

    void addPriorityDependency(Dependency dependency);
}
