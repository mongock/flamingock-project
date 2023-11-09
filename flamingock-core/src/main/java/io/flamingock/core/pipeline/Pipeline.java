package io.flamingock.core.pipeline;

import io.flamingock.core.task.filter.TaskFilter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class Pipeline {

    public static PipelineBuilder builder() {
        return new PipelineBuilder();
    }
    private final List<Stage> stages;

    private Pipeline(List<Stage> stages) {
        this.stages = stages;
    }

    public List<Stage> getStages() {
        return stages;
    }


    public static class PipelineBuilder {

        private final Collection<Stage> stages ;
        private final Collection<TaskFilter> taskFilters;

        private PipelineBuilder() {
            stages = new LinkedHashSet<>();
            taskFilters = new HashSet<>();
        }

        public PipelineBuilder addStages(Collection<Stage> stages) {
            this.stages.addAll(stages);
            return this;
        }

        public PipelineBuilder addStage(Stage stage) {
            stages.add(stage);
            return this;
        }

        public PipelineBuilder setFilters(Collection<TaskFilter> taskFilters) {
            this.taskFilters.addAll(taskFilters);
            return this;
        }

        public Pipeline build() {
            List<Stage> stagesWithTaskFilter = stages.stream()
                    .map(stage -> stage.addFilters(taskFilters))
                    .collect(Collectors.toList());
            return new Pipeline(stagesWithTaskFilter);
        }

    }
}
