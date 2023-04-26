package io.mongock.core.execution.summary;

import io.mongock.core.summary.Summary;
import io.mongock.core.summary.SummaryLine;

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
