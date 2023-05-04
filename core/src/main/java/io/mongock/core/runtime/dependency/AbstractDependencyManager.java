package io.mongock.core.runtime.dependency;

import io.mongock.core.runtime.dependency.exception.ForbiddenParameterException;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Stream;

public abstract class AbstractDependencyManager {

    private final LinkedHashSet<Dependency> priorityDependencies;

    public AbstractDependencyManager() {
        priorityDependencies = new LinkedHashSet<>();
    }

    public void addPriorityDependencies(Collection<? extends Dependency> dependencies) {
        dependencies.forEach(this::addPriorityDependency);
    }

    public void addPriorityDependency(Dependency dependency) {
        addDependency(priorityDependencies, dependency);
    }


    public Optional<Dependency> getDependency(Class<?> type) throws ForbiddenParameterException {
        return getDependency(type, null);
    }

    public Optional<Dependency> getDependency(Class<?> type, String name) throws ForbiddenParameterException {
        Optional<Dependency> priorityDependencyOptional = getDependencyFromStore(priorityDependencies, type, name);
        Optional<Dependency> dependencyOptional = priorityDependencyOptional.isPresent()
                ? priorityDependencyOptional
                : getStandardDependency(type, name);

        if (!dependencyOptional.isPresent()) {
            return Optional.empty();

        }
        Dependency dependency = dependencyOptional.get();
        return DependencyBuildable.class.isAssignableFrom(dependency.getClass())
                ? getDependency(((DependencyBuildable) dependency).getImplType())
                : dependencyOptional;
    }

    private static boolean isByName(String name) {
        return name != null && !name.isEmpty() && !Dependency.DEFAULT_NAME.equals(name);
    }

    protected final Optional<Dependency> getDependencyFromStore(Collection<Dependency> dependencyStore, Class<?> type, String name) {
        boolean byName = isByName(name);
        Predicate<Dependency> filter = byName
                ? dependency -> name.equals(dependency.getName())
                : dependency -> type.isAssignableFrom(dependency.getType());

        Stream<Dependency> stream = dependencyStore.stream().filter(filter);
        if (byName) {
            return stream.findFirst();
        } else {
            return stream.reduce((dependency1, dependency2) -> !dependency1.isDefaultNamed() && dependency2.isDefaultNamed() ? dependency2 : dependency1);
        }
    }

    protected <T extends Dependency> void addDependency(Collection<T> dependencyStore, T dependency) {
        //add returns false if it's already there. In that case, it needs to be removed and then inserted
        if (!dependencyStore.add(dependency)) {
            dependencyStore.remove(dependency);
            dependencyStore.add(dependency);
        }
    }

    abstract protected Optional<Dependency> getStandardDependency(Class<?> type, String name);

}
