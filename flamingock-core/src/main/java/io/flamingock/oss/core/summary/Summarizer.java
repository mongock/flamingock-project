package io.flamingock.oss.core.summary;

import io.flamingock.oss.core.execution.summary.StepSummary;

//No thread safe
public interface Summarizer<LINE extends SummaryLine> {

    Summarizer<LINE> add(LINE line);

    StepSummary getSummary();

}
