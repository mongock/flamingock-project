package io.mongock.core.summary;

import java.util.List;
import java.util.stream.Collectors;

public interface Summary {

    List<? extends SummaryLine> getLines();

    default String getPretty() {
        return getLines()
                .stream()
                .map(SummaryLine::getLine)
                .collect(Collectors.joining("\n"));
    }
}
