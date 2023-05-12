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


    abstract class AbstractStepNavigator implements StepNavigatorBuilder {


        protected StepSummarizer summarizer = null;
        protected AuditWriter<?> auditWriter = null;

        protected Lock lock = null;

        protected DependencyContext staticContext;

        protected TransactionWrapper transactionWrapper = null;

        public AbstractStepNavigator() {
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

        protected StepNavigator buildInstance(RuntimeManager runtimeManager) {
            return new StepNavigator(auditWriter, summarizer, runtimeManager, transactionWrapper);
        }

    }

}
