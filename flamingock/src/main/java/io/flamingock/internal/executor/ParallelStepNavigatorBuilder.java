package io.flamingock.internal.executor;

import io.mongock.core.execution.navigator.StepNavigator;
import io.mongock.core.execution.navigator.StepNavigatorBuilder;
import io.mongock.core.runtime.RuntimeManager;
import io.mongock.core.runtime.dependency.DefaultDependencyInjectableContext;
import io.mongock.core.runtime.dependency.DependencyInjectableContext;
import io.mongock.core.runtime.dependency.PriorityDependencyContext;


public class ParallelStepNavigatorBuilder extends StepNavigatorBuilder.AbstractStepNavigator {

    public ParallelStepNavigatorBuilder() {
    }


    @Override
    public StepNavigator build() {
        DependencyInjectableContext injectableContext = new PriorityDependencyContext(
                new DefaultDependencyInjectableContext(),
                staticContext);
        RuntimeManager runtimeManager = RuntimeManager.builder()
                .setDependencyContext(injectableContext)
                .setLock(lock)
                .build();
        return buildInstance(runtimeManager);
    }
}