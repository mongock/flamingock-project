package io.flamingock.core.pipeline;

import io.flamingock.core.pipeline.stage.StageDefinition;

import java.util.List;

public class PipelineDefinition {

    private final List<StageDefinition> stages;

    public PipelineDefinition(List<StageDefinition> stages) {
        this.stages = stages;
    }

    public List<StageDefinition> getStages() {
        return stages;
    }
}
