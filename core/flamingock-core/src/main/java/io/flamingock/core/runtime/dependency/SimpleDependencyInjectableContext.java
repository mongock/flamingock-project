package io.flamingock.core.runtime.dependency;

import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.function.Predicate;

public class SimpleDependencyInjectableContext extends AbstractDependencyContext implements DependencyInjectableContext {

    private final LinkedHashSet<Dependency> dependencyStore;

    public SimpleDependencyInjectableContext() {
        dependencyStore = new LinkedHashSet<>();
    }

    @Override
    protected Optional<Dependency> getFromStorage(Predicate<Dependency> filter) {
        return  dependencyStore.stream().filter(filter)
                .reduce((dependency1, dependency2) -> !dependency1.isDefaultNamed() && dependency2.isDefaultNamed() ? dependency2 : dependency1);
    }

    @Override
    public void addDependency(Dependency dependency) {
        if (!dependencyStore.add(dependency)) {
            dependencyStore.remove(dependency);
            dependencyStore.add(dependency);
        }
    }


}
