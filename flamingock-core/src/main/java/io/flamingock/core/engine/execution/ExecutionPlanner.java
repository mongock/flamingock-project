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

package io.flamingock.core.engine.execution;

import io.flamingock.core.engine.lock.Lock;
import io.flamingock.core.engine.lock.LockException;
import io.flamingock.core.pipeline.ExecutableStage;
import io.flamingock.core.pipeline.Pipeline;

import java.util.function.BiConsumer;

public abstract class ExecutionPlanner {

    abstract protected ExecutionPlan getNextExecution(Pipeline pipeline) throws LockException;

    public final boolean executeIfRequired(Pipeline pipeline, BiConsumer<Lock, ExecutableStage> consumer) {
        try(ExecutionPlan execution = getNextExecution(pipeline)) {
            if (execution.isExecutable()) {
                execution.applyOnEach(consumer);
                return true;
            } else {
                return false;
            }
        }
    }
}
