package io.flamingock.core.stage.execution;

import io.flamingock.core.audit.AuditWriter;
import io.flamingock.core.runtime.dependency.DependencyContext;
import io.flamingock.core.task.executable.ExecutableTask;
import io.flamingock.core.task.navigation.navigator.StepNavigationOutput;
import io.flamingock.core.task.navigation.navigator.StepNavigatorBuilder;
import io.flamingock.core.task.navigation.summary.DefaultStepSummarizer;
import io.flamingock.core.task.navigation.summary.ProcessSummary;
import io.flamingock.core.task.navigation.summary.StepSummary;
import io.flamingock.core.lock.Lock;
import io.flamingock.core.stage.ExecutableStage;
import io.flamingock.core.transaction.TransactionWrapper;
import io.flamingock.core.util.StreamUtil;

import java.util.Optional;
import java.util.stream.Stream;

public abstract class StageExecutor {
    protected final AuditWriter auditWriter;

    protected final TransactionWrapper transactionWrapper;
    private final DependencyContext dependencyContext;

    public StageExecutor(DependencyContext dependencyContext,
                                 AuditWriter auditWriter,
                                 TransactionWrapper transactionWrapper) {
        this.dependencyContext = dependencyContext;
        this.auditWriter = auditWriter;
        this.transactionWrapper = transactionWrapper;
    }


    public Output run(ExecutableStage executableProcess,
                      StageExecutionContext stageExecutionContext,
                      Lock lock) throws StageExecutionException {

        ProcessSummary summary = new ProcessSummary();

        StepNavigatorBuilder stepNavigatorBuilder = getStepNavigatorBuilder();

        Stream<StepNavigationOutput> taskStepStream = getTaskStream(executableProcess)
                .map(task -> stepNavigatorBuilder
                        .setAuditWriter(auditWriter)
                        .setStaticContext(dependencyContext)
                        .setLock(lock)
                        .setTransactionWrapper(transactionWrapper)
                        .setSummarizer(new DefaultStepSummarizer())//todo reuse Summarizer
                        .build()
                        .executeTask(task, stageExecutionContext)
                )
                .peek(summary::addSummary);

        try {
            Optional<StepNavigationOutput> failedOutput = StreamUtil.processUntil(taskStepStream, StepNavigationOutput::isFailed);
            failedOutput.ifPresent(failed -> {
                throw new StageExecutionException(summary);
            });

        } catch (Throwable throwable) {
            throw new StageExecutionException(throwable, summary);
        }

        return new Output(summary);
    }

    abstract protected Stream<? extends ExecutableTask> getTaskStream(ExecutableStage executableProcess);

    abstract protected StepNavigatorBuilder getStepNavigatorBuilder();

    class Output {

        private final StepSummary summary;

        public Output(StepSummary summary) {
            this.summary = summary;
        }

        public StepSummary getSummary() {
            return summary;
        }
    }
}
