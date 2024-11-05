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
import io.flamingock.core.api.metadata.FlamingockMetadata;
import io.flamingock.core.task.filter.TaskFilter;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class Pipeline {

    private final FlamingockMetadata metadata;

    private final List<Stage> stages;

    public static PipelineBuilder builder() {
        return new PipelineBuilder();
    }

    private Pipeline(List<Stage> stages, FlamingockMetadata metadata) {
        this.stages = stages;
        this.metadata = metadata;
    }

    public List<LoadedStage> getLoadedStages() {
        List<LoadedStage> loadedStages = stages
                .stream()
                .map(stage-> stage.load(metadata))
                .collect(Collectors.toList());
        validateStages(loadedStages);
        return loadedStages;
    }

    private static void validateStages(List<LoadedStage> loadedStages) {
        List<String> emptyLoadedStages= loadedStages
                .stream()
                .filter(stage-> stage.getTaskDescriptors().size() == 0)
                .map(LoadedStage::getName)
                .collect(Collectors.toList());
        if(emptyLoadedStages.size() > 0) {
            String emptyLoadedStagesString = String.join(",", emptyLoadedStages);
            throw new FlamingockException("There are empty stages: " + emptyLoadedStagesString);
        }
    }

    public static class PipelineBuilder {

        private final Collection<Stage> beforeUserStages = new LinkedHashSet<>();
        private final Collection<Stage> userStages = new LinkedHashSet<>();
        private final Collection<Stage> afterUserStages = new LinkedHashSet<>();
        private final Collection<TaskFilter> taskFilters = new LinkedHashSet<>();
        private FlamingockMetadata metadata;

        private PipelineBuilder() {
        }

        public PipelineBuilder addBeforeUserStages(Iterable<Stage> stages) {
            stages.forEach(userStages::add);
            return this;
        }

        public PipelineBuilder addUserStages(Iterable<Stage> stages) {
            stages.forEach(userStages::add);
            return this;
        }
        public PipelineBuilder addAfterUserStages(Iterable<Stage> stages) {
            stages.forEach(userStages::add);
            return this;
        }

        public PipelineBuilder addFilters(Collection<TaskFilter> taskFilters) {
            this.taskFilters.addAll(taskFilters);
            return this;
        }

        public PipelineBuilder setMetadata(FlamingockMetadata metadata) {
            this.metadata = metadata;
            return this;
        }

        public Pipeline build() {

            List<Stage> allSortedStages = new LinkedList<>(beforeUserStages);
            allSortedStages.addAll(userStages);
            allSortedStages.addAll(afterUserStages);

            List<Stage> stagesWithTaskFilter = allSortedStages.stream()
                    .map(stage -> stage.addFilters(taskFilters))
                    .collect(Collectors.toList());
            //If no metadata injected(normal behaviour), it will take the default, which is the one generated or null if not generated at all.
            FlamingockMetadata metadata1 = metadata != null ? metadata : FlamingockMetadata.getInstance().orElse(null);
            return new Pipeline(stagesWithTaskFilter, metadata1);
        }


    }
}
