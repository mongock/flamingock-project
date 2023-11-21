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

package io.flamingock.core.task.navigation.step.afteraudit;

import io.flamingock.core.task.navigation.step.AbstractTaskStep;
import io.flamingock.core.task.navigation.step.rolledback.ManualRolledBackStep;
import io.flamingock.core.runtime.RuntimeManager;
import io.flamingock.core.task.executable.Rollback;
import io.flamingock.core.util.StopWatch;

public final class RollableStep extends AbstractTaskStep {
    private final Rollback rollback;


    public RollableStep(Rollback rollback) {
        super(rollback.getTask());
        this.rollback = rollback;
    }

    public ManualRolledBackStep rollback(RuntimeManager runtimeHelper) {
        StopWatch stopWatch = StopWatch.start();
        try {
            rollback.rollback(runtimeHelper);
            stopWatch.stop();
            return ManualRolledBackStep.successfulRollback(rollback, stopWatch.getDuration());
        } catch (Throwable throwable) {
            stopWatch.stop();
            return  ManualRolledBackStep.failedRollback(rollback, stopWatch.getDuration(), throwable);
        }
    }


}