package io.mongock.core.execution.navigator;

import io.mongock.core.audit.writer.AuditWriter;
import io.mongock.core.execution.summary.StepSummarizer;
import io.mongock.core.lock.Lock;
import io.mongock.core.runtime.RuntimeManager;
import io.mongock.core.runtime.dependency.DefaultDependencyInjectableContext;
import io.mongock.core.runtime.dependency.DependencyContext;
import io.mongock.core.runtime.dependency.DependencyInjectableContext;
import io.mongock.core.runtime.dependency.PriorityDependencyContext;
import io.mongock.core.transaction.TransactionWrapper;

public interface StepNavigatorBuilder {


    StepNavigatorBuilder setSummarizer(StepSummarizer summarizer);

    StepNavigatorBuilder setAuditWriter(AuditWriter<?> auditWriter);

    StepNavigatorBuilder setLock(Lock lock);

    StepNavigatorBuilder setStaticContext(DependencyContext staticContext);

    StepNavigatorBuilder setTransactionWrapper(TransactionWrapper transactionWrapper);

    StepNavigator build();


    class ParallelStepNavigatorBuilder implements StepNavigatorBuilder {

        protected StepSummarizer summarizer = null;
        protected AuditWriter<?> auditWriter = null;

        protected Lock lock = null;

        protected DependencyContext staticContext;

        protected TransactionWrapper transactionWrapper = null;

        public ParallelStepNavigatorBuilder() {
        }


        @Override
        public StepNavigatorBuilder setSummarizer(StepSummarizer summarizer) {
            this.summarizer = summarizer;
            return this;
        }

        @Override
        public StepNavigatorBuilder setAuditWriter(AuditWriter<?> auditWriter) {
            this.auditWriter = auditWriter;
            return this;
        }

        @Override
        public StepNavigatorBuilder setStaticContext(DependencyContext staticContext) {
            this.staticContext = staticContext;
            return this;
        }

        @Override
        public StepNavigatorBuilder setLock(Lock lock) {
            this.lock = lock;
            return this;
        }

        @Override
        public StepNavigatorBuilder setTransactionWrapper(TransactionWrapper transactionWrapper) {
            this.transactionWrapper = transactionWrapper;
            return this;
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
            return new StepNavigator(auditWriter, summarizer, runtimeManager, transactionWrapper);
        }
    }


    class ReusableStepNavigatorBuilder extends ParallelStepNavigatorBuilder {

        private final StepNavigator stepNavigator = new StepNavigator(null, null, null, null);


        public ReusableStepNavigatorBuilder() {
        }

        @Override
        public StepNavigator build() {
            StepNavigator instance;
            instance = stepNavigator;
            instance.clean();
            if (summarizer != null) {
                summarizer.clear();
            }
            setBaseDependencies(instance);
            return instance;
        }

        private void setBaseDependencies(StepNavigator instance) {
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

}
