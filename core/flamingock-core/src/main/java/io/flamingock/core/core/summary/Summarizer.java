package io.flamingock.core.core.summary;

import io.flamingock.core.core.execution.summary.StepSummary;

//No thread safe
public interface Summarizer<LINE extends SummaryLine> {

    Summarizer<LINE> add(LINE line);

    StepSummary getSummary();

}
