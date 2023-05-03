package io.mongock.core.dependency;

import io.mongock.core.dependency.exception.ForbiddenParameterException;
import io.mongock.core.dependency.proxy.LockGuardProxyFactory;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class DependencyManagerImpl implements DependencyManager {

    private final LinkedHashSet<ChangeSetDependency> connectorDependencies;
    private final LinkedHashSet<ChangeSetDependency> standardDependencies;
    protected LockGuardProxyFactory lockGuardProxyFactory;

    public DependencyManagerImpl() {
        standardDependencies = new LinkedHashSet<>();
        connectorDependencies = new LinkedHashSet<>();
    }

    public Optional<Object> getDependency(Class<?> type, boolean lockGuarded) throws ForbiddenParameterException {
        return getDependency(type, null, lockGuarded);
    }

    public Optional<Object> getDependency(Class<?> type, String name, boolean lockGuarded) throws ForbiddenParameterException {
        Optional<ChangeSetDependency> dependencyOpt = getDependencyFromStore(connectorDependencies, type, name);
        if (!dependencyOpt.isPresent()) {
            dependencyOpt = getDependencyFromStore(standardDependencies, type, name);
        }
        if(!dependencyOpt.isPresent()) {
            return Optional.empty();
        }
        ChangeSetDependency dependency = dependencyOpt.get();
        if(ChangeSetDependencyBuildable.class.isAssignableFrom(dependency.getClass())) {
            ChangeSetDependencyBuildable buildable = (ChangeSetDependencyBuildable)dependency;
            Optional<Object> implOpt = getDependency(buildable.getImplType(), buildable.isProxeable());
            if(!implOpt.isPresent()) {
                return Optional.empty();
            }
            return implOpt.map(buildable.getDecoratorFunction());
        } else {

            return dependency.isProxeable() && lockGuarded
                    ? Optional.of(lockGuardProxyFactory.getRawProxy(dependency.getInstance(), type))
                    : Optional.ofNullable(dependency.getInstance());
        }


    }

    private Optional<ChangeSetDependency> getDependencyFromStore(Collection<ChangeSetDependency> dependencyStore, Class<?> type, String name) {
        boolean byName = name != null && !name.isEmpty() && !ChangeSetDependency.DEFAULT_NAME.equals(name);
        Predicate<ChangeSetDependency> filter = byName
                ? dependency -> name.equals(dependency.getName())
                : dependency -> type.isAssignableFrom(dependency.getType());

        Stream<ChangeSetDependency> stream = dependencyStore.stream().filter(filter);
        if (byName) {
            return stream.findFirst();
        } else {
            return stream.reduce((dependency1, dependency2) -> !dependency1.isDefaultNamed() && dependency2.isDefaultNamed() ? dependency2 : dependency1);
        }
    }

    // setters

    public DependencyManager setLockGuardProxyFactory(LockGuardProxyFactory lockGuardProxyFactory) {
        this.lockGuardProxyFactory = lockGuardProxyFactory;
        return this;
    }

    /**
     * This method will be called just before executing a changeSet, for all the changeSets
     * @param dependencies dependencies from driver
     * @return the current dependency manager
     */
    public DependencyManager addDriverDependencies(Collection<? extends ChangeSetDependency> dependencies) {
        dependencies.forEach(this::addDriverDependency);
        return this;
    }

    public DependencyManager addDriverDependency(ChangeSetDependency dependency) {
        return addDependency(connectorDependencies, dependency);
    }

    public DependencyManager addStandardDependencies(Collection<? extends ChangeSetDependency> dependencies) {
        dependencies.forEach(this::addStandardDependency);
        return this;
    }

    public DependencyManager addStandardDependency(ChangeSetDependency dependency) {
        return addDependency(standardDependencies, dependency);
    }

    private <T extends ChangeSetDependency> DependencyManager addDependency(Collection<T> dependencyStore, T dependency) {
        //add returns false if it's already there. In that case, it needs to be removed and then inserted
        if (!dependencyStore.add(dependency)) {
            dependencyStore.remove(dependency);
            dependencyStore.add(dependency);
        }
        return this;
    }

}
