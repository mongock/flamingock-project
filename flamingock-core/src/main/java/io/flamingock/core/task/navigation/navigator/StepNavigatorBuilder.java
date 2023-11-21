/*
 * Copyright 2023 Flamingock (https://oss.flamingock.io)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.flamingock.core.task.navigation.navigator;

import io.flamingock.core.audit.AuditWriter;
import io.flamingock.core.task.navigation.summary.StepSummarizer;
import io.flamingock.core.lock.Lock;
import io.flamingock.core.runtime.RuntimeManager;
import io.flamingock.core.runtime.dependency.DependencyContext;
import io.flamingock.core.transaction.TransactionWrapper;

public interface StepNavigatorBuilder {


    StepNavigatorBuilder setSummarizer(StepSummarizer summarizer);

    StepNavigatorBuilder setAuditWriter(AuditWriter auditWriter);

    StepNavigatorBuilder setLock(Lock lock);

    StepNavigatorBuilder setStaticContext(DependencyContext staticContext);

    StepNavigatorBuilder setTransactionWrapper(TransactionWrapper transactionWrapper);

    StepNavigator build();


    abstract class AbstractStepNavigator implements StepNavigatorBuilder {


        protected StepSummarizer summarizer = null;
        protected AuditWriter auditWriter = null;

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
        public StepNavigatorBuilder setAuditWriter(AuditWriter auditWriter) {
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