package io.flamingock.internal.executor;

import io.flamingock.oss.core.execution.navigator.StepNavigator;
import io.flamingock.oss.core.execution.navigator.StepNavigatorBuilder;
import io.flamingock.oss.core.runtime.RuntimeManager;
import io.flamingock.oss.core.runtime.dependency.DefaultDependencyInjectableContext;
import io.flamingock.oss.core.runtime.dependency.DependencyInjectableContext;
import io.flamingock.oss.core.runtime.dependency.PriorityDependencyContext;


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