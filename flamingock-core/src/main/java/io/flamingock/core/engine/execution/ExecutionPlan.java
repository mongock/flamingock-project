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
import io.flamingock.core.pipeline.ExecutableStage;

import java.util.Collection;
import java.util.function.BiConsumer;

public class ExecutionPlan implements AutoCloseable {


    private static final ExecutionPlan CONTINUE = new ExecutionPlan();

    public static ExecutionPlan newExecution(Lock lock, Collection<ExecutableStage> stages) {
        return new ExecutionPlan(lock, stages);
    }

    public static ExecutionPlan CONTINUE() {
        return CONTINUE;
    }

    private final boolean executable;

    private final Lock lock;

    private final Collection<ExecutableStage> stages;

    private ExecutionPlan(Lock lock, Collection<ExecutableStage> stages) {
        this.executable = true;
        this.lock = lock;
        this.stages = stages;
    }

    private ExecutionPlan() {
        executable = false;
        this.lock = null;
        this.stages = null;
    }

    public boolean isExecutable() {
        return executable;
    }

    public void applyOnEach(BiConsumer<Lock, ExecutableStage> consumer) {
        if (executable && stages != null) {
            stages.forEach(executableStage -> consumer.accept(lock, executableStage));
        }
    }

    @Override
    public void close() {
        if (lock != null) {
            lock.release();
        }
    }
}