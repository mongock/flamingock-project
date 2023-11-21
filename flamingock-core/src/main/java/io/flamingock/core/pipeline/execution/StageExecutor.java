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

package io.flamingock.core.pipeline.execution;

import io.flamingock.core.audit.AuditWriter;
import io.flamingock.core.lock.Lock;
import io.flamingock.core.pipeline.ExecutableStage;
import io.flamingock.core.runtime.dependency.DependencyContext;
import io.flamingock.core.task.executable.ExecutableTask;
import io.flamingock.core.task.navigation.navigator.ReusableStepNavigatorBuilder;
import io.flamingock.core.task.navigation.navigator.StepNavigationOutput;
import io.flamingock.core.task.navigation.navigator.StepNavigatorBuilder;
import io.flamingock.core.task.navigation.summary.DefaultStepSummarizer;
import io.flamingock.core.task.navigation.summary.StageSummary;
import io.flamingock.core.task.navigation.summary.StepSummary;
import io.flamingock.core.transaction.TransactionWrapper;

import java.util.stream.Stream;

public class StageExecutor {
    protected final AuditWriter auditWriter;

    protected final TransactionWrapper transactionWrapper;
    private final DependencyContext dependencyContext;

    private StageExecutor(DependencyContext dependencyContext, AuditWriter auditWriter) {
        this(dependencyContext, auditWriter, null);
    }

    public StageExecutor(DependencyContext dependencyContext,
                          AuditWriter auditWriter,
                          TransactionWrapper transactionWrapper) {
        this.dependencyContext = dependencyContext;
        this.auditWriter = auditWriter;
        this.transactionWrapper = transactionWrapper;
    }

    public Output execute(ExecutableStage executableStage,
                          StageExecutionContext stageExecutionContext,
                          Lock lock) throws StageExecutionException {

        StageSummary summary = new StageSummary();

        StepNavigatorBuilder stepNavigatorBuilder = getStepNavigatorBuilder(executableStage.isParallel());

        //TODO think that we can build the StepNavigator sequentially and then execute it in Parallel
        // this would save memory footprint

        try {
            getTaskStream(executableStage)
                    .map(task -> stepNavigatorBuilder
                            .setAuditWriter(auditWriter)
                            .setStaticContext(dependencyContext)
                            .setLock(lock)
                            .setTransactionWrapper(transactionWrapper)
                            .setSummarizer(new DefaultStepSummarizer())//todo reuse Summarizer
                            .build()
                            .executeTask(task, stageExecutionContext)
                    ).peek(summary::addSummary)
                    .filter(StepNavigationOutput::isFailed)
                    .findFirst()
                    .ifPresent(failed -> {
                        throw new StageExecutionException(summary);
                    });

        } catch (Throwable throwable) {
            throw new StageExecutionException(throwable, summary);
        }

        return new Output(summary);
    }

    protected Stream<? extends ExecutableTask> getTaskStream(ExecutableStage executableStage) {
        return executableStage.isParallel()
                ? executableStage.getTasks().parallelStream()
                : executableStage.getTasks().stream();
    }

    protected StepNavigatorBuilder getStepNavigatorBuilder(boolean parallel) {
        StepNavigatorBuilder immutableStepNavigatorBuilder = null;
        return parallel ? immutableStepNavigatorBuilder //TODO  implement ConcurrentStepNavigatorBuilder
                : new ReusableStepNavigatorBuilder();
    }

    public static class Output {

        private final StepSummary summary;

        public Output(StepSummary summary) {
            this.summary = summary;
        }

        public StepSummary getSummary() {
            return summary;
        }
    }
}