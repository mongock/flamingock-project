package io.mongock.core.execution.executor;

import io.mongock.core.audit.writer.AuditWriter;
import io.mongock.core.execution.navigator.StepNavigationOutput;
import io.mongock.core.execution.navigator.StepNavigatorBuilder;
import io.mongock.core.execution.summary.ProcessSummary;
import io.mongock.core.process.single.SingleExecutableProcess;
import io.mongock.core.runtime.RuntimeHelper;

import java.util.Optional;
import java.util.stream.Stream;

import static io.mongock.core.util.StreamUtil.processUntil;

public class SingleProcessExecutor implements ProcessExecutor<SingleExecutableProcess> {
    protected final AuditWriter<?> auditWriter;

    public SingleProcessExecutor(AuditWriter<?> auditWriter) {
        this.auditWriter = auditWriter;
    }

    @Override
    public Output run(SingleExecutableProcess executableProcess,
                      ExecutionContext executionContext,
                      RuntimeHelper runtimeHelper) throws ProcessExecutionException {
        ProcessSummary summary = new ProcessSummary();
        StepNavigatorBuilder navBuilder = StepNavigatorBuilder.reusableInstance();
        Stream<StepNavigationOutput> taskStepStream = executableProcess.getTasks()
                .stream()
                .map(task -> navBuilder
                        .setAuditWriter(auditWriter)
                        .setRuntimeHelper(runtimeHelper)
                        .build()
                        .start(task, executionContext)
                )
                .peek(summary::addSummary);
        Optional<StepNavigationOutput> failedOutput = processUntil(taskStepStream, StepNavigationOutput::isFailed);

        if (failedOutput.isPresent()) {
            throw new ProcessExecutionException(summary);
        }

        return new Output(summary);
    }

}
