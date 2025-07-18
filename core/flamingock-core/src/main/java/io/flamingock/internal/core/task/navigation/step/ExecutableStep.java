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

package io.flamingock.internal.core.task.navigation.step;

import io.flamingock.internal.core.task.navigation.step.execution.ExecutionStep;
import io.flamingock.internal.core.task.navigation.step.execution.FailedExecutionStep;
import io.flamingock.internal.core.task.navigation.step.execution.SuccessExecutionStep;
import io.flamingock.internal.core.runtime.RuntimeManager;
import io.flamingock.internal.util.StopWatch;

public class ExecutableStep extends AbstractTaskStep {

    public ExecutableStep(StartStep step) {
        super(step.getTask());
    }

    public ExecutionStep execute(RuntimeManager runtimeHelper) {
        StopWatch stopWatch = StopWatch.startAndGet();
        try {
            task.execute(runtimeHelper);
            return SuccessExecutionStep.instance(this, stopWatch.getElapsed());
        } catch (Throwable throwable) {
            return FailedExecutionStep.instance(this, stopWatch.getElapsed(), throwable);
        }

    }

}
