/*
 * Copyright 2023 Flamingock (https://oss.flamingock.io)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.flamingock.core.pipeline;

import io.flamingock.core.api.exception.FlamingockException;
import io.flamingock.core.task.TaskDescriptor;
import io.flamingock.core.task.filter.TaskFilter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class Pipeline implements PipelineDescriptor {


    private final List<LoadedStage> loadedStages;

    public static PipelineBuilder builder() {
        return new PipelineBuilder();
    }

    private Pipeline(List<LoadedStage> loadedStages) {
        this.loadedStages = loadedStages;
    }

    public List<LoadedStage> validateAndGetLoadedStages() {
        validateStages();
        return loadedStages;
    }

    public void validateStages() {
        List<String> emptyLoadedStages= loadedStages
                .stream()
                .filter(stage-> stage.getLoadedTasks().size() == 0)
                .map(LoadedStage::getName)
                .collect(Collectors.toList());
        if(emptyLoadedStages.size() > 0) {
            String emptyLoadedStagesString = String.join(",", emptyLoadedStages);
            throw new FlamingockException("There are empty stages: " + emptyLoadedStagesString);
        }
    }

    @Override
    public Optional<TaskDescriptor> getLoadedTask(String taskId) {
        return loadedStages.stream()
                .map(LoadedStage::getLoadedTasks)
                .flatMap(Collection::stream)
                .filter(loadedTask -> loadedTask.getId().equals(taskId))
                .findFirst();
    }

    @Override
    public Optional<String> getStageByTask(String taskId) {
        for(LoadedStage loadedStage: loadedStages) {
            for(TaskDescriptor loadedTask: loadedStage.getLoadedTasks()) {
                if(loadedTask.getId().equals(taskId)) {
                    return Optional.of(loadedStage.getName());
                }
            }
        }
        return Optional.empty();
    }


    public static class PipelineBuilder {

        private Collection<Stage> beforeUserStages = new LinkedHashSet<>();
        private PreviewPipeline previewPipeline;
        private Collection<Stage> afterUserStages = new LinkedHashSet<>();
        private Collection<TaskFilter> taskFilters = new LinkedHashSet<>();

        private PipelineBuilder() {
        }

        public PipelineBuilder addBeforeUserStages(Collection<Stage> stages) {
            this.beforeUserStages = stages;
            return this;
        }

        public PipelineBuilder addPreviewPipeline(PreviewPipeline previewPipeline) {
            this.previewPipeline = previewPipeline;
            return this;
        }
        public PipelineBuilder addAfterUserStages(Collection<Stage> stages) {
            this.afterUserStages = stages;
            return this;
        }

        public PipelineBuilder addFilters(Collection<TaskFilter> taskFilters) {
            this.taskFilters.addAll(taskFilters);
            return this;
        }


        public Pipeline build() {

            //TODO GET LOADED STAGES FROM Preview
            Collection<Stage> userStages = new ArrayList<>();

            List<Stage> allSortedStages = new LinkedList<>(beforeUserStages);
            allSortedStages.addAll(userStages);
            allSortedStages.addAll(afterUserStages);

            List<Stage> stagesWithTaskFilter = allSortedStages.stream()
                    .map(stage -> stage.addFilters(taskFilters))
                    .collect(Collectors.toList());

            List<LoadedStage> loadedStages = stagesWithTaskFilter
                    .stream()
                    .map(Stage::load)
                    .collect(Collectors.toList());
            return new Pipeline(loadedStages);
        }


    }
}
