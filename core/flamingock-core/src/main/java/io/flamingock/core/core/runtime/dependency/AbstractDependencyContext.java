package io.flamingock.core.core.runtime.dependency;

import io.flamingock.core.core.runtime.dependency.exception.ForbiddenParameterException;

import java.util.Optional;

public abstract class AbstractDependencyContext implements DependencyContext {

    @Override
    public Optional<Dependency> getDependency(Class<?> type) throws ForbiddenParameterException {
        return getDependency(type, null);
    }

    @Override
    public Optional<Dependency> getDependency(Class<?> type, String name) throws ForbiddenParameterException {
        Optional<Dependency> dependencyOptional = getDependencyInternal(type, name);
        if (!dependencyOptional.isPresent()) {
            return Optional.empty();

        }
        Dependency dependency = dependencyOptional.get();
        return DependencyBuildable.class.isAssignableFrom(dependency.getClass())
                ? getDependency(((DependencyBuildable) dependency).getImplType())
                : dependencyOptional;
    }

    protected abstract Optional<Dependency> getDependencyInternal(Class<?> type, String name);

}
