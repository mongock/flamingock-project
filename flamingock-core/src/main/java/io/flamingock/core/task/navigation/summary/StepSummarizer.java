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

package io.flamingock.core.task.navigation.summary;

import io.flamingock.core.task.descriptor.LoadedTask;
import io.flamingock.core.task.navigation.step.ExecutableStep;
import io.flamingock.core.task.navigation.step.afteraudit.AfterExecutionAuditStep;
import io.flamingock.core.task.navigation.step.complete.CompletedAlreadyAppliedStep;
import io.flamingock.core.task.navigation.step.complete.failed.CompletedFailedManualRollback;
import io.flamingock.core.task.navigation.step.execution.ExecutionStep;
import io.flamingock.core.task.navigation.step.rolledback.RolledBackStep;
import io.flamingock.core.summary.Summarizer;

//No thread safe
public interface StepSummarizer<SELF extends StepSummarizer<SELF>> extends Summarizer<StepSummaryLine> {

    void clear();

    Summarizer<StepSummaryLine> add(StepSummaryLine line);

    SELF add(ExecutableStep step);

    SELF add(ExecutionStep step);

    SELF add(AfterExecutionAuditStep step);

    SELF add(RolledBackStep step);

    SELF add(CompletedFailedManualRollback step);

    SELF add(CompletedAlreadyAppliedStep ignoredStep);

    SELF addNotReachedTask(LoadedTask taskDescriptor);



    StepSummary getSummary();
}
