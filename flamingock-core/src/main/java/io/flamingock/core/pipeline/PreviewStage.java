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

import io.flamingock.commons.utils.FileUtil;
import io.flamingock.core.task.TaskDescriptor;
import io.flamingock.core.task.preview.builder.TemplatePreviewTaskBuilder;
import io.flamingock.core.task.preview.CodedPreviewChangeUnit;
import io.flamingock.core.task.preview.ReflectionPreviewTask;
import io.flamingock.core.task.preview.TemplatePreviewChangeUnit;
import io.flamingock.template.TemplateTaskDefinition;

import java.io.File;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * This class represents the process defined by the user in the builder, yaml, etc.
 * It doesn't necessary contain directly the tasks, it can contain the code packages, etc.
 */

public class PreviewStage {

    private  String name;

    private  String description;

    private String sourcesPackage;

    private String resourcesDir;

    private Collection<ReflectionPreviewTask> tasks;

    private boolean parallel;

    public PreviewStage() {
    }

    public PreviewStage(String name,
                        String description,
                        String sourcesPackage,
                        String resourcesDir,
                        Collection<ReflectionPreviewTask> tasks,
                        boolean parallel) {
        this.name = name;
        this.description = description;
        this.sourcesPackage = sourcesPackage;
        this.resourcesDir = resourcesDir;
        this.tasks = tasks;
        this.parallel = parallel;
    }

    public static Builder builder() {
        return new Builder();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSourcesPackage() {
        return sourcesPackage;
    }

    public void setSourcesPackage(String sourcesPackage) {
        this.sourcesPackage = sourcesPackage;
    }

    public String getResourcesDir() {
        return resourcesDir;
    }

    public void setResourcesDir(String resourcesDir) {
        this.resourcesDir = resourcesDir;
    }

    public Collection<ReflectionPreviewTask> getTasks() {
        return tasks;
    }

    public void setTasks(Collection<ReflectionPreviewTask> tasks) {
        this.tasks = tasks;
    }

    public boolean isParallel() {
        return parallel;
    }

    public void setParallel(boolean parallel) {
        this.parallel = parallel;
    }

    public LoadedStage load() {
        return null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PreviewStage stage = (PreviewStage) o;
        return Objects.equals(name, stage.name);
    }

    @Override
    public int hashCode() {
        return name != null ? name.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "PreviewStage{" + "name='" + name + '\'' +
                ", tasks=" + tasks +
                ", parallel=" + parallel +
                '}';
    }

    public static class Builder {
        private String name;

        private String description;

        private Collection<String> sourcesRoots;

        private String sourcesPackage;

        private String resourcesRoot;

        private String resourcesDir;

        private Collection<CodedPreviewChangeUnit> changeUnitClasses;

        private boolean parallel = false;

        private Builder() {
        }

        public Builder setName(String name) {
            this.name = name;
            return this;
        }

        public Builder setDescription(String description) {
            this.description = description;
            return this;
        }

        public Builder setSourcesRoots(Collection<String> sourcesRoots) {
            this.sourcesRoots = sourcesRoots;
            return this;
        }

        public Builder setSourcesPackage(String sourcesPackage) {
            this.sourcesPackage = sourcesPackage;
            return this;
        }

        public Builder setResourcesRoot(String resourcesRoot) {
            this.resourcesRoot = resourcesRoot;
            return this;
        }

        public Builder setResourcesDir(String resourcesDir) {
            this.resourcesDir = resourcesDir;
            return this;
        }

        public Builder setChangeUnitClasses(Collection<CodedPreviewChangeUnit> changeUnitClasses) {
            this.changeUnitClasses = changeUnitClasses;
            return this;
        }

        public Builder setParallel(boolean parallel) {
            this.parallel = parallel;
            return this;
        }


        public PreviewStage build() {

            Collection<File> resourcesDirectories = new LinkedList<>();

            if (resourcesDir != null) {
                File resourcesDirectory = FileUtil.getFile(resourcesRoot, resourcesDir, false);
                if(FileUtil.isExistingDir(resourcesDirectory)) {
                    resourcesDirectories.add(resourcesDirectory);
                }
            }

            if (sourcesPackage != null) {
                String formattedSourcePackage = sourcesPackage.replace(".", "/");
                sourcesRoots.stream()
                        .map(sourceRoot -> FileUtil.getFile(sourceRoot, formattedSourcePackage, false))
                        .filter(FileUtil::isExistingDir)
                        .forEach(resourcesDirectories::add);
            }

            if (name == null || name.isEmpty()) {
                throw new RuntimeException("Stage requires name");
            }

            Collection<CodedPreviewChangeUnit> changeUnitClassesList = changeUnitClasses != null
                    ? changeUnitClasses
                    : Collections.emptyList();

            if (resourcesDirectories.isEmpty() && changeUnitClassesList.isEmpty()) {
                throw new RuntimeException("No changeUnits provided for stage: " + name);
            }

            Collection<TemplatePreviewChangeUnit> templatedTasksDescriptors = getTemplatedTaskDescriptors(resourcesDirectories);
            Collection<ReflectionPreviewTask> allDescriptors = mergeDescriptors(templatedTasksDescriptors, changeUnitClassesList);

            return new PreviewStage(name, description, sourcesPackage, resourcesDir, allDescriptors, parallel);
        }




        private Collection<ReflectionPreviewTask> mergeDescriptors(Collection<TemplatePreviewChangeUnit> templatedDescriptors,
                                                                   Collection<CodedPreviewChangeUnit> descriptorsFromClasses) {

            Collection<ReflectionPreviewTask> descriptors = Stream
                    .concat(templatedDescriptors.stream(), descriptorsFromClasses.stream())
                    .collect(Collectors.toList());

            if (descriptors.stream().allMatch(TaskDescriptor::isSortable)) {
                //if all descriptors are sorted, we return a sorted collection
                return descriptors.stream().sorted().collect(Collectors.toList());

            } else if (descriptors.parallelStream().anyMatch(TaskDescriptor::isSortable)) {
                //if at least one of them are sorted, but not all. An exception is thrown
                throw new IllegalArgumentException("Either all tasks are ordered or none is");

            } else {
                //if none of the tasks are sorted, an unsorted collection is returned
                return descriptors;
            }
        }


        private Collection<TemplatePreviewChangeUnit> getTemplatedTaskDescriptors(Collection<File> resourcesDirectories) {
            if (resourcesDirectories == null) {
                return Collections.emptyList();
            }
            return resourcesDirectories
                    .stream()
                    .map(FileUtil::getAllYamlFiles)
                    .flatMap(Collection::stream)
                    .map(file -> FileUtil.getFromYamlFile(file, TemplateTaskDefinition.class))
                    .map(TemplatePreviewTaskBuilder.recycledBuilder()::setFromDefinition)
                    .map(TemplatePreviewTaskBuilder::getPreview)
                    .collect(Collectors.toList());
        }
    }


}
