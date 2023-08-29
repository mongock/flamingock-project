package io.flamingock.core.task.navigation.summary;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class StageSummary implements StepSummary {

    private final List<StepSummary> stageSummaries = new LinkedList<>();

    public void addSummary(StepSummary summary) {
        stageSummaries.add(summary);
    }

    public List<StepSummary> getSummaries() {
        return stageSummaries;
    }

    @Override
    public List<StepSummaryLine> getLines() {
        return getSummaries().stream()
                .map(StepSummary::getLines)
                .flatMap(List::stream)
                .collect(Collectors.toList());
    }
}
