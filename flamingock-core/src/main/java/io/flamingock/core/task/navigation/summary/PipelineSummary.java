package io.flamingock.core.task.navigation.summary;

import io.flamingock.core.pipeline.Pipeline;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PipelineSummary implements StepSummary {

    private final Pipeline pipeline;
    private final Map<String, StageSummary> processedStageSummaries = new HashMap<>();


    public PipelineSummary(Pipeline pipeline) {
        this.pipeline = pipeline;
    }

    public void add(StageSummary stageSummary) {
        processedStageSummaries.put(stageSummary.getId(), stageSummary);
    }

    @Override
    public List<StageSummary> getLines() {
        return Collections.emptyList();



//        return processedStageSummaries;
    }
}
