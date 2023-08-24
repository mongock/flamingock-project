package io.flamingock.core.task.navigation.summary;

import io.flamingock.core.summary.Summary;
import io.flamingock.core.summary.SummaryLine;

import java.util.List;
import java.util.stream.Collectors;

public interface StepSummary extends Summary {

    List<StepSummaryLine> getLines();

    @Override
    default String getPretty() {
        return getLines()
                .stream()
                .map(SummaryLine::getLine)
                .collect(Collectors.joining("\n"));
    }
}
