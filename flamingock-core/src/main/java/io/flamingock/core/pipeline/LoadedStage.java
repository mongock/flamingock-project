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

import io.flamingock.core.engine.audit.writer.AuditEntry;
import io.flamingock.core.engine.audit.writer.AuditStageStatus;
import io.flamingock.core.task.descriptor.LoadedTask;
import io.flamingock.core.task.executable.ExecutableTask;
import io.flamingock.core.task.executable.ParentExecutableTaskFactory;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * It's the result of adding the loaded task to the ProcessDefinition
 */
public class LoadedStage {

    private final String name;

    private final Collection<LoadedTask> loadedTasks;

    private final boolean parallel;

    private final ParentExecutableTaskFactory factory;

    public LoadedStage(String name,
                       Collection<LoadedTask> loadedTasks,
                       boolean parallel) {
        this.name = name;
        this.loadedTasks = loadedTasks;
        this.parallel = parallel;
        this.factory = ParentExecutableTaskFactory.INSTANCE;

    }

    public ExecutableStage applyState(AuditStageStatus state) {

        Map<String, AuditEntry.Status> statesMap = state.getEntryStatesMap();

        List<ExecutableTask> tasks = loadedTasks
                .stream()
                .map(loadedTask -> factory.extractTasks(name, loadedTask, statesMap.get(loadedTask.getId())))
                .flatMap(List::stream)
                .collect(Collectors.toCollection(LinkedList::new));

        return new ExecutableStage(name, tasks, parallel);
    }

    public String getName() {
        return name;
    }


    public Collection<LoadedTask> getLoadedTasks() {
        return loadedTasks;
    }

    public boolean isParallel() {
        return parallel;
    }
}
