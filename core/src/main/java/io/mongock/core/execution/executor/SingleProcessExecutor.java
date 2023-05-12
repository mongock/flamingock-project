package io.mongock.core.execution.executor;

import io.mongock.core.audit.writer.AuditWriter;
import io.mongock.core.execution.navigator.StepNavigationOutput;
import io.mongock.core.execution.navigator.StepNavigatorBuilder;
import io.mongock.core.execution.summary.DefaultStepSummarizer;
import io.mongock.core.execution.summary.ProcessSummary;
import io.mongock.core.process.single.SingleExecutableProcess;
import io.mongock.core.runtime.RuntimeOrchestrator;
import io.mongock.core.transaction.TransactionWrapper;

import java.util.Optional;
import java.util.stream.Stream;

import static io.mongock.core.util.StreamUtil.processUntil;

public class SingleProcessExecutor implements ProcessExecutor<SingleExecutableProcess> {
    protected final AuditWriter<?> auditWriter;

    protected final TransactionWrapper transactionWrapper;

    public SingleProcessExecutor(AuditWriter<?> auditWriter) {
        this(auditWriter, null);
    }

    public SingleProcessExecutor(AuditWriter<?> auditWriter,
                                 TransactionWrapper transactionWrapper) {
        this.auditWriter = auditWriter;
        this.transactionWrapper = transactionWrapper;
    }

    @Override
    public Output run(SingleExecutableProcess executableProcess,
                      ExecutionContext executionContext,
                      RuntimeOrchestrator runtimeHelper) throws ProcessExecutionException {
        ProcessSummary summary = new ProcessSummary();
        StepNavigatorBuilder navBuilder = StepNavigatorBuilder.reusableInstance();

        Stream<StepNavigationOutput> taskStepStream = executableProcess.getTasks()
                .stream()
                .map(task -> navBuilder
                        .setAuditWriter(auditWriter)
                        .setRuntimeHelper(runtimeHelper)
                        .setTransactionWrapper(transactionWrapper)
                        .setSummarizer(new DefaultStepSummarizer())//todo reuse Summarizer
                        .build()
                        .start(task, executionContext)
                )
                .peek(summary::addSummary);

        try {
            Optional<StepNavigationOutput> failedOutput = processUntil(taskStepStream, StepNavigationOutput::isFailed);
            if (failedOutput.isPresent()) {
                throw new ProcessExecutionException(summary);
            }
        } catch (Throwable throwable) {
            throw new ProcessExecutionException(throwable, summary);
        }


        return new Output(summary);
    }

}
