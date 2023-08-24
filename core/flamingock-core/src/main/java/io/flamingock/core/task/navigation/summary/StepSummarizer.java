package io.flamingock.core.task.navigation.summary;

import io.flamingock.core.task.navigation.step.afteraudit.AfterExecutionAuditStep;
import io.flamingock.core.task.navigation.step.complete.CompletedAlreadyAppliedStep;
import io.flamingock.core.task.navigation.step.complete.failed.CompletedFailedManualRollback;
import io.flamingock.core.task.navigation.step.execution.ExecutionStep;
import io.flamingock.core.task.navigation.step.rolledback.RolledBackStep;
import io.flamingock.core.summary.Summarizer;

//No thread safe
public interface StepSummarizer extends Summarizer<StepSummaryLine> {

    void clear();

    Summarizer<StepSummaryLine> add(StepSummaryLine line);

    StepSummarizer add(ExecutionStep step);

    StepSummarizer add(AfterExecutionAuditStep step);

    StepSummarizer add(RolledBackStep step);

    StepSummarizer add(CompletedFailedManualRollback step);

    StepSummarizer add(CompletedAlreadyAppliedStep ignoredStep);

    StepSummary getSummary();
}
