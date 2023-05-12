package io.mongock.core.execution.executor;

import io.mongock.core.audit.writer.AuditWriter;
import io.mongock.core.execution.navigator.StepNavigationOutput;
import io.mongock.core.execution.navigator.StepNavigatorBuilder;
import io.mongock.core.execution.summary.DefaultStepSummarizer;
import io.mongock.core.execution.summary.ProcessSummary;
import io.mongock.core.lock.Lock;
import io.mongock.core.process.single.SingleExecutableProcess;
import io.mongock.core.runtime.dependency.AbstractDependencyManager;
import io.mongock.core.task.executable.ExecutableTask;
import io.mongock.core.transaction.TransactionWrapper;

import java.util.Optional;
import java.util.stream.Stream;

import static io.mongock.core.util.StreamUtil.processUntil;

public abstract class AbstractSingleProcessExecutor implements ProcessExecutor<SingleExecutableProcess> {
    protected final AuditWriter<?> auditWriter;

    protected final TransactionWrapper transactionWrapper;
    private final AbstractDependencyManager dependencyManager;

    public AbstractSingleProcessExecutor(AbstractDependencyManager dependencyManager,
                                         AuditWriter<?> auditWriter) {
        this(dependencyManager, auditWriter, null);
    }

    public AbstractSingleProcessExecutor(AbstractDependencyManager dependencyManager,
                                         AuditWriter<?> auditWriter,
                                         TransactionWrapper transactionWrapper) {
        this.dependencyManager = dependencyManager;
        this.auditWriter = auditWriter;
        this.transactionWrapper = transactionWrapper;
    }

    @Override
    public Output run(SingleExecutableProcess executableProcess,
                      ExecutionContext executionContext,
                      Lock lock) throws ProcessExecutionException {

        ProcessSummary summary = new ProcessSummary();

        StepNavigatorBuilder stepNavigatorBuilder = getStepNavigatorBuilder();

        Stream<StepNavigationOutput> taskStepStream = buildTaskStream(executableProcess)
                .map(task -> stepNavigatorBuilder
                        .setAuditWriter(auditWriter)
                        .setDependencyManager(dependencyManager)
                        .setLock(lock)
                        .setTransactionWrapper(transactionWrapper)
                        .setSummarizer(new DefaultStepSummarizer())//todo reuse Summarizer
                        .build()
                        .start(task, executionContext)
                )
                .peek(summary::addSummary);

        try {
            Optional<StepNavigationOutput> failedOutput = processUntil(taskStepStream, StepNavigationOutput::isFailed);
            failedOutput.ifPresent(failed -> {
                throw new ProcessExecutionException(summary);
            });

        } catch (Throwable throwable) {
            throw new ProcessExecutionException(throwable, summary);
        }

        return new Output(summary);
    }

    abstract protected Stream<? extends ExecutableTask> buildTaskStream(SingleExecutableProcess executableProcess);

    abstract protected StepNavigatorBuilder getStepNavigatorBuilder();

}
