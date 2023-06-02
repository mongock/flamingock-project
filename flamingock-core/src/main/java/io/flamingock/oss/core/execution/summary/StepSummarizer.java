package io.flamingock.oss.core.execution.summary;

import io.flamingock.oss.core.execution.step.afteraudit.AfterExecutionAuditStep;
import io.flamingock.oss.core.execution.step.complete.CompletedAlreadyAppliedStep;
import io.flamingock.oss.core.execution.step.complete.failed.CompletedFailedManualRollback;
import io.flamingock.oss.core.execution.step.execution.ExecutionStep;
import io.flamingock.oss.core.execution.step.rolledback.RolledBackStep;
import io.flamingock.oss.core.summary.Summarizer;

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
