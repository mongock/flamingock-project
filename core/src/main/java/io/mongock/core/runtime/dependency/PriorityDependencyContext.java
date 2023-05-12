package io.mongock.core.runtime.dependency;

import io.mongock.core.runtime.dependency.exception.ForbiddenParameterException;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;

public class PriorityDependencyContext implements DependencyInjectableContext {

    private final DependencyContext baseContext;

    private final DependencyInjectableContext priorityInjectableContext;

    public PriorityDependencyContext(DependencyInjectableContext priorityInjectableContext, DependencyContext baseContext) {
        this.priorityInjectableContext = priorityInjectableContext;
        this.baseContext = baseContext;
    }

    @Override
    public Optional<Dependency> getDependency(Class<?> type) throws ForbiddenParameterException {
        Optional<Dependency> priorityDependency = priorityInjectableContext.getDependency(type);
        return priorityDependency.isPresent() ? priorityDependency : baseContext.getDependency(type);

    }

    @Override
    public Optional<Dependency> getDependency(Class<?> type, String name) throws ForbiddenParameterException {
        Optional<Dependency> priorityDependency = priorityInjectableContext.getDependency(type, name);
        return priorityDependency.isPresent() ? priorityDependency : baseContext.getDependency(type, name);
    }

    @Override
    public List<Dependency> getAllDependencies() {
        List<Dependency> allDependencies = baseContext.getAllDependencies();
        allDependencies.addAll(priorityInjectableContext.getAllDependencies());
        return allDependencies;
    }

    @Override
    public void addDependency(Dependency dependency) {
        priorityInjectableContext.addDependency(dependency);
    }
}
