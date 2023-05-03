package io.mongock.core.runtime.dependency;

import io.mongock.core.runtime.dependency.exception.ForbiddenParameterException;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class DependencyManagerImpl implements DependencyManager {

    private final LinkedHashSet<Dependency> connectorDependencies;
    private final LinkedHashSet<Dependency> standardDependencies;

    public DependencyManagerImpl() {
        standardDependencies = new LinkedHashSet<>();
        connectorDependencies = new LinkedHashSet<>();
    }

    public Optional<Dependency> getDependency(Class<?> type) throws ForbiddenParameterException {
        return getDependency(type, null);
    }

    public Optional<Dependency> getDependency(Class<?> type, String name) throws ForbiddenParameterException {
        Optional<Dependency> connectorDependencyOpt = getDependencyFromStore(connectorDependencies, type, name);
        Optional<Dependency> dependencyOpt = !connectorDependencyOpt.isPresent()
                ? getDependencyFromStore(standardDependencies, type, name)
                : connectorDependencyOpt;
        if (!dependencyOpt.isPresent()) {
            return Optional.empty();
        }
        Dependency dependency = dependencyOpt.get();
        return DependencyBuildable.class.isAssignableFrom(dependency.getClass())
                ? getDependency(((DependencyBuildable) dependency).getImplType())
                : dependencyOpt;

    }

    private Optional<Dependency> getDependencyFromStore(Collection<Dependency> dependencyStore, Class<?> type, String name) {
        boolean byName = name != null && !name.isEmpty() && !Dependency.DEFAULT_NAME.equals(name);
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

    /**
     * This method will be called just before executing a changeSet, for all the changeSets
     *
     * @param dependencies dependencies from driver
     * @return the current dependency manager
     */
    public DependencyManager addDriverDependencies(Collection<? extends Dependency> dependencies) {
        dependencies.forEach(this::addDriverDependency);
        return this;
    }

    public DependencyManager addDriverDependency(Dependency dependency) {
        return addDependency(connectorDependencies, dependency);
    }

    public DependencyManager addStandardDependencies(Collection<? extends Dependency> dependencies) {
        dependencies.forEach(this::addStandardDependency);
        return this;
    }

    public DependencyManager addStandardDependency(Dependency dependency) {
        return addDependency(standardDependencies, dependency);
    }

    private <T extends Dependency> DependencyManager addDependency(Collection<T> dependencyStore, T dependency) {
        //add returns false if it's already there. In that case, it needs to be removed and then inserted
        if (!dependencyStore.add(dependency)) {
            dependencyStore.remove(dependency);
            dependencyStore.add(dependency);
        }
        return this;
    }

}
