package io.flamingock.core.core.runtime.dependency;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class DefaultDependencyInjectableContext extends AbstractDependencyInjectableContext {

    private final LinkedHashSet<Dependency> dependencyStorage;

    public DefaultDependencyInjectableContext() {
        this(new ArrayList<>());
    }

    public DefaultDependencyInjectableContext(DependencyContext dependencyContext) {
        this(dependencyContext.getAllDependencies());
    }

    public DefaultDependencyInjectableContext(List<Dependency> dependencyList) {
        dependencyStorage = new LinkedHashSet<>(dependencyList);
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

    @Override
    public List<Dependency> getAllDependencies() {
        return new CopyOnWriteArrayList<>(dependencyStorage);
    }
}
