package io.mongock.core.summary;

import io.mongock.core.execution.summary.StepSummary;

//No thread safe
public interface Summarizer<LINE extends SummaryLine> {

    Summarizer<LINE> add(LINE line);

    StepSummary getSummary();

}
