package io.flamingock.core.core.runtime.dependency;

import io.flamingock.core.core.runtime.dependency.exception.ForbiddenParameterException;

import java.util.Optional;
import java.util.function.Predicate;

public abstract class AbstractDependencyContext implements DependencyContext {

    @Override
    public Optional<Dependency> getDependency(Class<?> type) throws ForbiddenParameterException {
        return getDependency(dependency -> type.isAssignableFrom(dependency.getType()));
    }

    @Override
    public Optional<Dependency> getDependency(String name) throws ForbiddenParameterException {
        if(name == null || name.isEmpty() || Dependency.DEFAULT_NAME.equals(name)) {
            throw new IllegalArgumentException("name cannot be null/empty  when retrieving dependency by name");
        }
        return getDependency(dependency -> name.equals(dependency.getName()));
    }

    private Optional<Dependency> getDependency(Predicate<Dependency> filter) throws ForbiddenParameterException {

        Optional<Dependency> dependencyOptional = getFromStorage(filter);
        if (!dependencyOptional.isPresent()) {
            return Optional.empty();

        }
        Dependency dependency = dependencyOptional.get();
        return DependencyBuildable.class.isAssignableFrom(dependency.getClass())
                ? getDependency(((DependencyBuildable) dependency).getImplType())
                : dependencyOptional;
    }


    protected abstract Optional<Dependency> getFromStorage(Predicate<Dependency> filter );

}
