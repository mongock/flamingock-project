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

package io.flamingock.core.runner;

import io.flamingock.core.engine.audit.AuditWriter;
import io.flamingock.core.configurator.core.CoreConfigurable;
import io.flamingock.core.event.EventPublisher;
import io.flamingock.core.engine.execution.ExecutionPlanner;
import io.flamingock.core.pipeline.Pipeline;
import io.flamingock.core.runtime.dependency.DependencyContext;
import io.flamingock.core.pipeline.execution.StageExecutionContext;
import io.flamingock.core.pipeline.execution.StageExecutor;
import io.flamingock.core.transaction.TransactionWrapper;
import io.flamingock.core.util.StringUtil;

public final class PipelineRunnerCreator {

    private PipelineRunnerCreator() {
    }

    public static Runner create(RunnerId runnerId,
                                Pipeline pipeline,
                                AuditWriter auditWriter,
                                TransactionWrapper transactionWrapper,
                                ExecutionPlanner executionPlanner,
                                CoreConfigurable coreConfiguration,
                                EventPublisher eventPublisher,
                                DependencyContext dependencyContext,
                                boolean isThrowExceptionIfCannotObtainLock,
                                Runnable finalizer) {
        //Instantiated here, so we don't wait until Runner.run() and fail fast
        final StageExecutor stageExecutor = new StageExecutor(dependencyContext, auditWriter, transactionWrapper);
        return new PipelineRunner(runnerId, executionPlanner, stageExecutor, buildExecutionContext(coreConfiguration), eventPublisher, isThrowExceptionIfCannotObtainLock) {
            @Override
            public void run() {
                try {
                    this.run(pipeline);
                } finally {
                    finalizer.run();
                }
            }
        };
    }

    private static StageExecutionContext buildExecutionContext(CoreConfigurable configuration) {
        return new StageExecutionContext(StringUtil.executionId(), StringUtil.hostname(), configuration.getDefaultAuthor(), configuration.getMetadata());
    }

}
