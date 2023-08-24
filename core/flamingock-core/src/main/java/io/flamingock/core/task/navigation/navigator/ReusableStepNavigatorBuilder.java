package io.flamingock.core.task.navigation.navigator;

import io.flamingock.core.runtime.RuntimeManager;
import io.flamingock.core.runtime.dependency.DependencyInjectableContext;
import io.flamingock.core.runtime.dependency.PriorityDependencyInjectableContext;

public class ReusableStepNavigatorBuilder extends StepNavigatorBuilder.AbstractStepNavigator {

    private final StepNavigator instance = new StepNavigator(null, null, null, null);


    public ReusableStepNavigatorBuilder() {
    }


    @Override
    public StepNavigator build() {
        instance.clean();
        if (summarizer != null) {
            summarizer.clear();
        }
        setBaseDependencies();
        return instance;
    }

    private void setBaseDependencies() {
        instance.setSummarizer(summarizer);
        instance.setAuditWriter(auditWriter);

        DependencyInjectableContext injectableContext = new PriorityDependencyInjectableContext(staticContext);
        RuntimeManager runtimeManager = RuntimeManager.builder()
                .setDependencyContext(injectableContext)
                .setLock(lock)
                .build();
        instance.setRuntimeManager(runtimeManager);
        instance.setTransactionWrapper(transactionWrapper);
    }
}