package io.mongock.core.execution.navigator;

import io.mongock.core.audit.writer.AuditWriter;
import io.mongock.core.execution.summary.StepSummarizer;
import io.mongock.core.lock.Lock;
import io.mongock.core.runtime.RuntimeManager;
import io.mongock.core.runtime.dependency.DependencyContext;
import io.mongock.core.transaction.TransactionWrapper;

public interface StepNavigatorBuilder {


    StepNavigatorBuilder setSummarizer(StepSummarizer summarizer);

    StepNavigatorBuilder setAuditWriter(AuditWriter<?> auditWriter);

    StepNavigatorBuilder setLock(Lock lock);

    StepNavigatorBuilder setDependencyContext(DependencyContext dependencyContext);

    StepNavigatorBuilder setTransactionWrapper(TransactionWrapper transactionWrapper);

    StepNavigator build();


    class DefaultStepNavigatorBuilder implements StepNavigatorBuilder {

        protected StepSummarizer summarizer = null;
        protected AuditWriter<?> auditWriter = null;

        protected Lock lock = null;

        protected DependencyContext dependencyContext;

        protected TransactionWrapper transactionWrapper = null;

        public DefaultStepNavigatorBuilder() {
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
        public StepNavigatorBuilder setDependencyContext(DependencyContext dependencyContext) {
            this.dependencyContext = dependencyContext;
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
            RuntimeManager runtimeManager = RuntimeManager.generator()
                    .setDependencyContext(dependencyContext)
                    .setLock(lock)
                    .generate();
            return new StepNavigator(auditWriter, summarizer, runtimeManager, transactionWrapper);
        }
    }


    class ReusableStepNavigatorBuilder extends DefaultStepNavigatorBuilder {

        private StepNavigator stepNavigator = new StepNavigator(null, null, null, null);


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
            RuntimeManager runtimeManager = RuntimeManager.generator()
                    .setDependencyContext(dependencyContext)
                    .setLock(lock)
                    .generate();
            instance.setRuntimeManager(runtimeManager);
            instance.setTransactionWrapper(transactionWrapper);
        }
    }

}
