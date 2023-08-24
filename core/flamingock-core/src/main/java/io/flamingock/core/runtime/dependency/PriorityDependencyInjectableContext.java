package io.flamingock.core.runtime.dependency;

import io.flamingock.core.runtime.dependency.exception.ForbiddenParameterException;

import java.util.Optional;

public class PriorityDependencyInjectableContext implements DependencyInjectableContext {

    private final DependencyContext baseContext;

    private final DependencyInjectableContext priorityInjectableContext;

    public PriorityDependencyInjectableContext(DependencyContext baseContext) {
        this(new SimpleDependencyInjectableContext(), baseContext);
    }

    public PriorityDependencyInjectableContext(DependencyInjectableContext priorityInjectableContext, DependencyContext baseContext) {
        this.priorityInjectableContext = priorityInjectableContext;
        this.baseContext = baseContext;
    }

    @Override
    public Optional<Dependency> getDependency(Class<?> type) throws ForbiddenParameterException {
        Optional<Dependency> priorityDependency = priorityInjectableContext.getDependency(type);
        return priorityDependency.isPresent() ? priorityDependency : baseContext.getDependency(type);

    }

    @Override
    public Optional<Dependency> getDependency(String name) throws ForbiddenParameterException {
        Optional<Dependency> priorityDependency = priorityInjectableContext.getDependency(name);
        return priorityDependency.isPresent() ? priorityDependency : baseContext.getDependency(name);
    }

    @Override
    public void addDependency(Dependency dependency) {
        priorityInjectableContext.addDependency(dependency);
    }
}
