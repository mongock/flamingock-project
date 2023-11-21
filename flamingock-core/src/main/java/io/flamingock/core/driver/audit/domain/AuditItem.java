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

package io.flamingock.core.driver.audit.domain;

import io.flamingock.core.pipeline.execution.StageExecutionContext;
import io.flamingock.core.task.descriptor.TaskDescriptor;

public class AuditItem {


    public enum Operation {EXECUTION, ROLLBACK}

    private final Operation operation;
    private final TaskDescriptor taskDescriptor;
    private final StageExecutionContext stageExecutionContext;
    private final RuntimeContext runtimeContext;

    public AuditItem(Operation operation,
                     TaskDescriptor taskDescriptor,
                     StageExecutionContext stageExecutionContext,
                     RuntimeContext runtimeContext) {
        this.operation = operation;
        this.taskDescriptor = taskDescriptor;
        this.stageExecutionContext = stageExecutionContext;
        this.runtimeContext = runtimeContext;
    }

    public Operation getOperation() {
        return operation;
    }

    public TaskDescriptor getTaskDescriptor() {
        return taskDescriptor;
    }

    public StageExecutionContext getExecutionContext() {
        return stageExecutionContext;
    }

    public RuntimeContext getRuntimeContext() {
        return runtimeContext;
    }

}