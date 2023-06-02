package io.flamingock.core.core.execution.navigator;

import io.flamingock.core.core.runtime.RuntimeManager;
import io.flamingock.core.core.runtime.dependency.DefaultDependencyInjectableContext;
import io.flamingock.core.core.runtime.dependency.DependencyInjectableContext;

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

        DependencyInjectableContext injectableContext = new DefaultDependencyInjectableContext(staticContext);
        RuntimeManager runtimeManager = RuntimeManager.builder()
                .setDependencyContext(injectableContext)
                .setLock(lock)
                .build();
        instance.setRuntimeManager(runtimeManager);
        instance.setTransactionWrapper(transactionWrapper);
    }
}