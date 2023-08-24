package io.flamingock.core.task.navigation.navigator;

import io.flamingock.core.task.navigation.summary.StepSummary;
import io.flamingock.core.task.navigation.summary.StepSummaryLine;

import java.util.List;

public class StepNavigationOutput implements StepSummary {

    private final boolean success;

    private final StepSummary summary;

    public StepNavigationOutput(boolean success, StepSummary summary) {
        this.summary = summary;
        this.success = success;
    }

    @Override
    public List<StepSummaryLine> getLines() {
        return summary.getLines();
    }

    @Override
    public String getPretty() {
        return summary.getPretty();
    }


    public boolean isFailed() {
        return !success;
    }
}
