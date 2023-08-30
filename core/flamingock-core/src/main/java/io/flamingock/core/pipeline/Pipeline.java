package io.flamingock.core.pipeline;

import io.flamingock.core.pipeline.stage.Stage;

import java.util.List;

public class Pipeline {

    private final List<Stage> stages;

    public Pipeline(List<Stage> stages) {
        this.stages = stages;
    }

    public List<Stage> getStages() {
        return stages;
    }
}
