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

package io.flamingock.core.task.executable.template;

import io.flamingock.core.engine.audit.writer.AuditEntry;
import io.flamingock.core.task.loaded.change.TemplateLoadedChangeUnit;
import io.flamingock.core.task.executable.ExecutableTaskFactory;

import java.util.Collections;
import java.util.List;


/**
 * Factory for ChangeUnit classes
 */
public class TemplateExecutableTaskFactory implements ExecutableTaskFactory<TemplateLoadedChangeUnit> {

    @Override
    public List<TemplateExecutableTask> extractTasks(String stageName, TemplateLoadedChangeUnit descriptor, AuditEntry.Status initialState) {
        return Collections.singletonList(getTasksFromReflection(stageName, descriptor, initialState));
    }

    private TemplateExecutableTask getTasksFromReflection(String stageName,
                                                          TemplateLoadedChangeUnit loadedTask,
                                                          AuditEntry.Status initialState) {
        return new TemplateExecutableTask(
                stageName,
                loadedTask,
                AuditEntry.Status.isRequiredExecution(initialState),
                loadedTask.getExecutionMethod(),
                loadedTask.getRollbackMethod().orElse(null),
                loadedTask.getConfigSetter().orElse(null),
                loadedTask.getConfigValidator().orElse(null)
        );

    }


}