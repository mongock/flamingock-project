package io.mongock.core.runtime.dependency;

import io.mongock.core.runtime.dependency.exception.ForbiddenParameterException;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class DependencyManagerWithContext implements DependencyManager {

    private final LinkedHashSet<Dependency> priorityDependencies;
    //    private final LinkedHashSet<Dependency> standardDependencies;
    private final DependencyContext context;

    public DependencyManagerWithContext(DependencyContext context) {
        this.context = context;
        priorityDependencies = new LinkedHashSet<>();
    }

    public Optional<Dependency> getDependency(Class<?> type) throws ForbiddenParameterException {
        return getDependency(type, null);
    }

    public Optional<Dependency> getDependency(Class<?> type, String name) throws ForbiddenParameterException {
        Optional<Dependency> connectorDependencyOpt = getDependencyFromStore(priorityDependencies, type, name);
        Optional<Dependency> dependencyOpt = !connectorDependencyOpt.isPresent()
                ? getFromContext(type, name)
                : connectorDependencyOpt;
        if (!dependencyOpt.isPresent()) {
            return Optional.empty();

        } else {
            Dependency dependency = dependencyOpt.get();
            return DependencyBuildable.class.isAssignableFrom(dependency.getClass())
                    ? getDependency(((DependencyBuildable) dependency).getImplType())
                    : dependencyOpt;
        }
    }

    private Optional<Dependency> getFromContext(Class<?> type, String name) {
        Object value = isByName(name)
                ? context.getBean(name)
                : context.getBean(type);
        if (value == null) {
            return Optional.empty();
        } else {
            return Optional.of(new Dependency(name, type, value, true));
        }
    }

    private static boolean isByName(String name) {
        return name != null && !name.isEmpty() && !Dependency.DEFAULT_NAME.equals(name);
    }

    private Optional<Dependency> getDependencyFromStore(Collection<Dependency> dependencyStore, Class<?> type, String name) {
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

    /**
     * This method will be called just before executing a changeSet, for all the changeSets
     *
     * @param dependencies dependencies from driver
     * @return the current dependency manager
     */
    public DependencyManager addPriorityDependencies(Collection<? extends Dependency> dependencies) {
        dependencies.forEach(this::addPriorityDependency);
        return this;
    }

    public DependencyManager addPriorityDependency(Dependency dependency) {
        return addDependency(priorityDependencies, dependency);
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
