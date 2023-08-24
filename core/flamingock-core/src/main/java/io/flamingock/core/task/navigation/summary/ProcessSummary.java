package io.flamingock.core.task.navigation.summary;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class ProcessSummary implements StepSummary {

    private final List<StepSummary> processSummaries = new LinkedList<>();

    public void addSummary(StepSummary summary) {
        processSummaries.add(summary);
    }

    public List<StepSummary> getSummaries() {
        return processSummaries;
    }

    @Override
    public List<StepSummaryLine> getLines() {
        return getSummaries().stream()
                .map(StepSummary::getLines)
                .flatMap(List::stream)
                .collect(Collectors.toList());
    }
}
