package io.mongock.core.execution.summary;

import io.mongock.core.execution.step.afteraudit.AfterExecutionAuditStep;
import io.mongock.core.execution.step.complete.CompletedAlreadyAppliedStep;
import io.mongock.core.execution.step.complete.failed.CompletedFailedManualRollback;
import io.mongock.core.execution.step.execution.ExecutionStep;
import io.mongock.core.execution.step.rolledback.ManualRolledBackStep;
import io.mongock.core.execution.step.rolledback.RolledBackStep;
import io.mongock.core.task.descriptor.TaskDescriptor;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

//NO thread-safe
public class DefaultStepSummarizer implements StepSummarizer {

    private final List<StepSummaryLine> lines = new LinkedList<>();

    @Override
    public StepSummarizer add(StepSummaryLine line) {
        lines.add(line);
        return this;
    }

    @Override
    public StepSummarizer add(ExecutionStep step) {
        return addStep(step.getTask().getDescriptor(), new StepSummaryLine.ExecutedSummaryLine(step));
    }

    @Override
    public StepSummarizer add(AfterExecutionAuditStep step) {
        return addStep(step.getTask().getDescriptor(), new StepSummaryLine.AfterExecutionAuditSummaryLine(step));
    }

    @Override
    public StepSummarizer add(RolledBackStep step) {
        return addStep(step.getTask().getDescriptor(), new StepSummaryLine.RolledBackSummaryLine(step));
    }

    @Override
    public StepSummarizer add(CompletedFailedManualRollback step) {
        return addStep(step.getTask().getDescriptor(), new StepSummaryLine.FailedCompletedManualRollbackSummaryLine(step));
    }

    @Override
    public StepSummarizer add(CompletedAlreadyAppliedStep step) {
        return addStep(step.getTask().getDescriptor(), new StepSummaryLine.AlreadyAppliedSummaryLine(step));
    }

    @Override
    public StepSummary getSummary() {
        return () -> new CopyOnWriteArrayList<>(lines);
    }

    private StepSummarizer addStep(TaskDescriptor taskDescriptor, StepSummaryLine step) {
        if (lines.isEmpty()) {
            add(new StepSummaryLine.InitialSummaryLine(taskDescriptor));
        }
        return add(step);
    }
}
