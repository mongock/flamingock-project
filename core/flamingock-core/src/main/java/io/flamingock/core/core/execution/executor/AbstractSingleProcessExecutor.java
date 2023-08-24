package io.flamingock.core.core.execution.executor;

import io.flamingock.core.core.audit.AuditWriter;
import io.flamingock.core.core.execution.navigator.StepNavigationOutput;
import io.flamingock.core.core.execution.navigator.StepNavigatorBuilder;
import io.flamingock.core.core.execution.summary.DefaultStepSummarizer;
import io.flamingock.core.core.execution.summary.ProcessSummary;
import io.flamingock.core.core.lock.Lock;
import io.flamingock.core.core.runtime.dependency.DependencyContext;
import io.flamingock.core.core.stage.ExecutableStage;
import io.flamingock.core.core.task.executable.ExecutableTask;
import io.flamingock.core.core.transaction.TransactionWrapper;
import io.flamingock.core.core.util.StreamUtil;

import java.util.Optional;
import java.util.stream.Stream;

public abstract class AbstractSingleProcessExecutor implements ProcessExecutor<ExecutableStage> {
    protected final AuditWriter auditWriter;

    protected final TransactionWrapper transactionWrapper;
    private final DependencyContext dependencyContext;

    public AbstractSingleProcessExecutor(DependencyContext dependencyContext,
                                         AuditWriter auditWriter) {
        this(dependencyContext, auditWriter, null);
    }

    public AbstractSingleProcessExecutor(DependencyContext dependencyContext,
                                         AuditWriter auditWriter,
                                         TransactionWrapper transactionWrapper) {
        this.dependencyContext = dependencyContext;
        this.auditWriter = auditWriter;
        this.transactionWrapper = transactionWrapper;
    }

    @Override
    public Output run(ExecutableStage executableProcess,
                      ExecutionContext executionContext,
                      Lock lock) throws ProcessExecutionException {

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
                        .executeTask(task, executionContext)
                )
                .peek(summary::addSummary);

        try {
            Optional<StepNavigationOutput> failedOutput = StreamUtil.processUntil(taskStepStream, StepNavigationOutput::isFailed);
            failedOutput.ifPresent(failed -> {
                throw new ProcessExecutionException(summary);
            });

        } catch (Throwable throwable) {
            throw new ProcessExecutionException(throwable, summary);
        }

        return new Output(summary);
    }

    abstract protected Stream<? extends ExecutableTask> getTaskStream(ExecutableStage executableProcess);

    abstract protected StepNavigatorBuilder getStepNavigatorBuilder();

}
