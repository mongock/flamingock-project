package io.mongock.core.execution.summary;

import io.mongock.core.execution.step.afteraudit.AfterExecutionAuditStep;
import io.mongock.core.execution.step.complete.CompleteFailedStep;
import io.mongock.core.execution.step.execution.ExecutionStep;
import io.mongock.core.execution.step.rolledback.RolledBackStep;
import io.mongock.core.summary.Summarizer;

//No thread safe
public interface StepSummarizer extends Summarizer<StepSummaryLine> {

    Summarizer<StepSummaryLine> add(StepSummaryLine line);

    StepSummarizer add(ExecutionStep step);

    StepSummarizer add(AfterExecutionAuditStep step);

    StepSummarizer add(RolledBackStep step);

    StepSummarizer add(CompleteFailedStep step);

    StepSummary getSummary();

}
