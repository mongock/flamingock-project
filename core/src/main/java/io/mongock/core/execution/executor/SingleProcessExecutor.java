package io.mongock.core.execution.executor;

import io.mongock.core.audit.writer.AuditWriter;
import io.mongock.core.execution.navigator.StepNavigationOutput;
import io.mongock.core.execution.navigator.StepNavigator;
import io.mongock.core.execution.summary.ProcessSummary;
import io.mongock.core.process.single.SingleExecutableProcess;
import io.mongock.core.util.RuntimeHelper;

import java.util.stream.Stream;

import static io.mongock.core.util.StreamUtil.processUntil;

public class SingleProcessExecutor implements ProcessExecutor<SingleExecutableProcess> {
    protected final AuditWriter<?> stateSaver;

    public SingleProcessExecutor(AuditWriter<?> stateSaver) {
        this.stateSaver = stateSaver;
    }

    @Override
    public Output run(SingleExecutableProcess executableProcess,
                      ExecutionContext executionContext,
                      RuntimeHelper runtimeHelper) {
        ProcessSummary summary = new ProcessSummary();
        Stream<StepNavigationOutput> taskStepStream = executableProcess.getTasks()
                .stream()
                .map(task -> StepNavigator.startByReuse(task, stateSaver, runtimeHelper, executionContext))
                .peek(summary::addSummary);//summary
        processUntil(taskStepStream, StepNavigationOutput::isFailed);
        return new Output(summary);
    }

}
