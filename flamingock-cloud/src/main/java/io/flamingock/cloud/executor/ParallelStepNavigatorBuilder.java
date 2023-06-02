package io.flamingock.cloud.executor;

import io.flamingock.core.core.execution.navigator.StepNavigator;
import io.flamingock.core.core.execution.navigator.StepNavigatorBuilder;
import io.flamingock.core.core.runtime.RuntimeManager;
import io.flamingock.core.core.runtime.dependency.DefaultDependencyInjectableContext;
import io.flamingock.core.core.runtime.dependency.DependencyInjectableContext;
import io.flamingock.core.core.runtime.dependency.PriorityDependencyContext;


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