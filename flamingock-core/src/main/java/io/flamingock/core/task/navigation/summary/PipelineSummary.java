package io.flamingock.core.task.navigation.summary;

import java.util.LinkedList;
import java.util.List;

public class PipelineSummary implements StepSummary {

    private final List<StageSummary> stageSummaries = new LinkedList<>();

    public void add(StageSummary stageSummary) {
        stageSummaries.add(stageSummary);
    }

    @Override
    public List<StageSummary> getLines() {
        return stageSummaries;
    }
}
