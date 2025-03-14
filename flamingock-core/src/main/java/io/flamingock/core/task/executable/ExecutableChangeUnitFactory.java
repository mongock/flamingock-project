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

package io.flamingock.core.task.executable;

import io.flamingock.commons.utils.StringUtil;
import io.flamingock.core.engine.audit.writer.AuditEntry;
import io.flamingock.core.task.descriptor.change.LoadedChangeUnit;
import io.flamingock.core.task.descriptor.ReflectionLoadedTask;

import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;


/**
 * Factory for ChangeUnit classes
 */
public class ExecutableChangeUnitFactory implements ExecutableTaskFactory<LoadedChangeUnit> {

    @Override
    public List<ReflectionExecutableTask<ReflectionLoadedTask>> extractTasks(String stageName, LoadedChangeUnit descriptor, AuditEntry.Status initialState) {
        return getTasksFromReflection(stageName, descriptor, initialState);
    }

    private List<ReflectionExecutableTask<ReflectionLoadedTask>> getTasksFromReflection(String stageName,
                                                                                        LoadedChangeUnit loadedTask,
                                                                                        AuditEntry.Status initialState) {

        Method executionMethod = loadedTask.getExecutionMethod();

        Optional<Method> rollbackMethodOpt = loadedTask.getRollbackMethod();
        ReflectionExecutableTask<ReflectionLoadedTask> task = new ReflectionExecutableTask<>(
                stageName,
                loadedTask,
                AuditEntry.Status.isRequiredExecution(initialState),
                executionMethod,
                rollbackMethodOpt.orElse(null));


            /*
            If there is any rollback dependency(@BeforeExecution in ChangeUnits, legacy ChangeSet, etc.) they are added as normal task
            before the main task and, if it also provides @BeforeExecutionRollback, it's also added to the main task's rollbackChain,
            so they  are rolled back in case the main task fails.
             */
        List<ReflectionExecutableTask<ReflectionLoadedTask>> tasks = new LinkedList<>();
        getBeforeExecutionOptional(stageName, task, initialState).ifPresent(beforeExecutionTask -> {
            tasks.add(beforeExecutionTask);
            beforeExecutionTask.getRollbackChain().forEach(task::addRollback);
        });
        tasks.add(task);
        return tasks;
    }

    private Optional<ReflectionExecutableTask<ReflectionLoadedTask>> getBeforeExecutionOptional(String stageName,
                                                                                                ReflectionExecutableTask<ReflectionLoadedTask> baseTask,
                                                                                                AuditEntry.Status initialState) {
        //Creates a new LoadedTask, based on the main one, but with the "beforeExecution id, also based on the main one"
        LoadedChangeUnit loadedTask = new LoadedChangeUnit(
                StringUtil.getBeforeExecutionId(baseTask.getDescriptor().getId()),
                baseTask.getDescriptor().getOrder().orElse(null),
                baseTask.getDescriptor().getSourceClass(),
                baseTask.getDescriptor().isRunAlways(),
                false,//A beforeExecution task will never be transactional,
                false//is not new
        );

        Optional<Method> beforeExecutionMethodOptional = loadedTask.getBeforeExecutionMethod();
        if (!beforeExecutionMethodOptional.isPresent()) {
            return Optional.empty();
        }
        Method beforeExecutionMethod = beforeExecutionMethodOptional.get();
        Optional<Method> rollbackBeforeExecution = loadedTask.getRollbackBeforeExecutionMethod();

        return Optional.of(new ReflectionExecutableTask<>(
                stageName,
                loadedTask,
                AuditEntry.Status.isRequiredExecution(initialState),
                beforeExecutionMethod,
                rollbackBeforeExecution.orElse(null)));

    }


}