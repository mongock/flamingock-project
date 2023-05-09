package io.mongock.core.execution.navigator;

import io.mongock.core.audit.writer.AuditWriter;
import io.mongock.core.execution.summary.StepSummarizer;
import io.mongock.core.runtime.RuntimeHelper;
import io.mongock.core.transaction.TransactionWrapper;

public interface StepNavigatorBuilder {

    static StepNavigatorBuilder reusableInstance() {
        return new ReusableStepNavigatorBuilder();
    }

    //For parallel execution.
    static StepNavigatorBuilder instance() {
        return new DefaultStepNavigatorBuilder();
    }


    StepNavigatorBuilder setSummarizer(StepSummarizer summarizer);

    StepNavigatorBuilder setAuditWriter(AuditWriter<?> auditWriter);

    StepNavigatorBuilder setRuntimeHelper(RuntimeHelper runtimeHelper);

    StepNavigatorBuilder setTransactionWrapper(TransactionWrapper transactionWrapper);

    AbstractStepNavigator build();


    class DefaultStepNavigatorBuilder implements StepNavigatorBuilder {

        protected StepSummarizer summarizer = null;
        protected AuditWriter<?> auditWriter = null;

        protected RuntimeHelper runtimeHelper = null;

        protected TransactionWrapper transactionWrapper = null;

        DefaultStepNavigatorBuilder() {
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
        public StepNavigatorBuilder setRuntimeHelper(RuntimeHelper runtimeHelper) {
            this.runtimeHelper = runtimeHelper;
            return this;
        }

        @Override
        public StepNavigatorBuilder setTransactionWrapper(TransactionWrapper transactionWrapper) {
            this.transactionWrapper = transactionWrapper;
            return this;
        }

        @Override
        public AbstractStepNavigator build() {
            AbstractStepNavigator instance;
            if (transactionWrapper != null) {
                instance = new TransactionalStepNavigator();
                ((TransactionalStepNavigator) instance).setTransactionWrapper(transactionWrapper);
            } else {
                instance = new StepNavigator();
            }
            setBaseDependencies(instance);
            return instance;
        }

        protected void setBaseDependencies(AbstractStepNavigator instance) {
            instance.setSummarizer(summarizer);
            instance.setAuditWriter(auditWriter);
            instance.setRuntimeHelper(runtimeHelper);
        }
    }


    final class ReusableStepNavigatorBuilder extends DefaultStepNavigatorBuilder {

        private StepNavigator stepNavigator = new StepNavigator();

        private TransactionalStepNavigator transactionalStepNavigator = new TransactionalStepNavigator();

        private ReusableStepNavigatorBuilder() {
        }

        @Override
        public AbstractStepNavigator build() {
            AbstractStepNavigator instance;
            if (transactionWrapper != null) {
                instance = transactionalStepNavigator;
                instance.clean();
                transactionalStepNavigator.setTransactionWrapper(transactionWrapper);
            } else {
                instance = stepNavigator;
                instance.clean();
            }
            setBaseDependencies(instance);
            return instance;
        }
    }

}
