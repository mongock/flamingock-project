package io.flamingock.core.summary;

import io.flamingock.core.task.navigation.summary.StepSummary;

//No thread safe
public interface Summarizer<LINE extends SummaryLine> {

    Summarizer<LINE> add(LINE line);

    StepSummary getSummary();

}
