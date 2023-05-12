package io.mongock.core.runtime.dependency;

import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class DefaultDependencyInjectableContext extends AbstractDependencyInjectableContext {

    private final LinkedHashSet<Dependency> dependencyStorage;

    public DefaultDependencyInjectableContext() {
        dependencyStorage = new LinkedHashSet<>();
    }

    @Override
    protected Optional<Dependency> getDependencyInternal(Class<?> type, String name) {
        boolean isByName = name != null && !name.isEmpty() && !Dependency.DEFAULT_NAME.equals(name);
        Predicate<Dependency> filter = isByName
                ? dependency -> name.equals(dependency.getName())
                : dependency -> type.isAssignableFrom(dependency.getType());

        Stream<Dependency> stream = dependencyStorage.stream().filter(filter);
        if (isByName) {
            return stream.findFirst();
        } else {
            return stream.reduce((dependency1, dependency2) -> !dependency1.isDefaultNamed() && dependency2.isDefaultNamed() ? dependency2 : dependency1);
        }
    }

    @Override
    public void addDependency(Dependency dependency) {
        addDependencyToStore(dependencyStorage, dependency);
    }

}
