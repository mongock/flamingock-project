package io.mongock.core.execution.executor;

import io.mongock.core.audit.writer.AuditWriter;
import io.mongock.core.execution.navigator.StepNavigationOutput;
import io.mongock.core.execution.navigator.StepNavigatorBuilder;
import io.mongock.core.execution.summary.DefaultStepSummarizer;
import io.mongock.core.execution.summary.ProcessSummary;
import io.mongock.core.lock.Lock;
import io.mongock.core.process.single.SingleExecutableProcess;
import io.mongock.core.runtime.RuntimeManager;
import io.mongock.core.runtime.dependency.AbstractDependencyManager;
import io.mongock.core.transaction.TransactionWrapper;

import java.util.Optional;
import java.util.stream.Stream;

import static io.mongock.core.util.StreamUtil.processUntil;

public class SingleProcessExecutor implements ProcessExecutor<SingleExecutableProcess> {
    protected final AuditWriter<?> auditWriter;

    protected final TransactionWrapper transactionWrapper;
    private final AbstractDependencyManager dependencyManager;

    public SingleProcessExecutor(AbstractDependencyManager dependencyManager, AuditWriter<?> auditWriter) {
        this(dependencyManager, auditWriter, null);
    }

    public SingleProcessExecutor(AbstractDependencyManager dependencyManager,
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

        StepNavigatorBuilder navBuilder = StepNavigatorBuilder.reusableInstance();

        Stream<StepNavigationOutput> taskStepStream = executableProcess.getTasks()
                .stream()
                .map(task -> navBuilder
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

}
