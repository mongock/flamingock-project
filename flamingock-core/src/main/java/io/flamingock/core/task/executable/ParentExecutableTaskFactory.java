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

import io.flamingock.core.engine.audit.writer.AuditEntry;
import io.flamingock.core.task.descriptor.change.ChangeUnitDescriptor;
import io.flamingock.core.task.descriptor.TaskDescriptor;
import io.flamingock.core.task.descriptor.change.TemplatedChangeUnitDescriptor;
import io.flamingock.core.task.executable.template.TemplatedExecutableTaskFactory;

import java.util.List;

/**
 * This is an Abstract factory of factories. Depending on the descriptor it will use one of the factories,
 * that could be ChangeUnitFactory, PluginFactory(not implemented yet), etc.
 */
public class ParentExecutableTaskFactory implements ExecutableTaskFactory<TaskDescriptor> {

    public static final ParentExecutableTaskFactory INSTANCE = new ParentExecutableTaskFactory();

    private static final ExecutableChangeUnitFactory executableChangeUnitFactory = new ExecutableChangeUnitFactory();

    private static final TemplatedExecutableTaskFactory executableTemplatedFactory = new TemplatedExecutableTaskFactory();

    private ParentExecutableTaskFactory() {
    }

    @Override
    public List<? extends ExecutableTask> extractTasks(String stageName, TaskDescriptor taskDescriptor, AuditEntry.Status initialState) {
        if(ChangeUnitDescriptor.class.isAssignableFrom(taskDescriptor.getClass())) {
            return executableChangeUnitFactory.extractTasks(stageName, (ChangeUnitDescriptor)taskDescriptor, initialState);
        } else if(TemplatedChangeUnitDescriptor.class.isAssignableFrom(taskDescriptor.getClass())) {
            return executableTemplatedFactory.extractTasks(stageName, (TemplatedChangeUnitDescriptor)taskDescriptor, initialState);
        } else {
            throw new IllegalArgumentException(String.format("ExecutableTask type not recognised[%s]", taskDescriptor.getClass().getName()));
        }
    }



}