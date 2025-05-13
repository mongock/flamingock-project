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

package io.flamingock.core.task.executable.builder;

import io.flamingock.commons.utils.StringUtil;
import io.flamingock.core.engine.audit.writer.AuditEntry;
import io.flamingock.core.task.executable.ReflectionExecutableTask;
import io.flamingock.core.task.loaded.AbstractLoadedTask;
import io.flamingock.core.task.loaded.LoadedTaskBuilder;
import io.flamingock.core.task.loaded.CodeLoadedChangeUnit;
import io.flamingock.core.task.loaded.AbstractReflectionLoadedTask;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;


/**
 * Factory for ChangeUnit classes
 */
public class CodeExecutableTaskBuilder implements ExecutableTaskBuilder<CodeLoadedChangeUnit> {
    private static final CodeExecutableTaskBuilder instance = new CodeExecutableTaskBuilder();

    private String stageName;
    private AuditEntry.Status initialState;
    private CodeLoadedChangeUnit loadedTask;

    static CodeExecutableTaskBuilder getInstance() {
        return instance;
    }

    public static boolean supports(AbstractLoadedTask loadedTask) {
        return CodeLoadedChangeUnit.class.isAssignableFrom(loadedTask.getClass());
    }


    @Override
    public CodeLoadedChangeUnit cast(AbstractLoadedTask loadedTask) {
        return (CodeLoadedChangeUnit)loadedTask;
    }

    @Override
    public CodeExecutableTaskBuilder setLoadedTask(CodeLoadedChangeUnit loadedTask) {
        this.loadedTask = loadedTask;
        return this;
    }

    @Override
    public CodeExecutableTaskBuilder setStageName(String stageName) {
        this.stageName = stageName;
        return this;
    }

    @Override
    public CodeExecutableTaskBuilder setInitialState(AuditEntry.Status initialState) {
        this.initialState = initialState;
        return this;
    }

    @Override
    public List<ReflectionExecutableTask<AbstractReflectionLoadedTask>> build() {
        return getTasksFromReflection(stageName, loadedTask, initialState);
    }

    private List<ReflectionExecutableTask<AbstractReflectionLoadedTask>> getTasksFromReflection(String stageName,
                                                                                                CodeLoadedChangeUnit loadedTask,
                                                                                                AuditEntry.Status initialState) {

        Method executionMethod = loadedTask.getExecutionMethod();

        Optional<Method> rollbackMethodOpt = loadedTask.getRollbackMethod();
        ReflectionExecutableTask<AbstractReflectionLoadedTask> task = new ReflectionExecutableTask<>(
                stageName,
                loadedTask,
                AuditEntry.Status.isRequiredExecution(initialState),
                executionMethod,
                rollbackMethodOpt.orElse(null));

        return Collections.singletonList(task);
    }

}