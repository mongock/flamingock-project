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

import io.flamingock.core.task.descriptor.TaskDescriptor;
import io.flamingock.core.task.navigation.step.ExecutableStep;
import io.flamingock.core.task.navigation.step.afteraudit.AfterExecutionAuditStep;
import io.flamingock.core.task.navigation.step.complete.CompletedAlreadyAppliedStep;
import io.flamingock.core.task.navigation.step.complete.failed.CompletedFailedManualRollback;
import io.flamingock.core.task.navigation.step.execution.ExecutionStep;
import io.flamingock.core.task.navigation.step.rolledback.RolledBackStep;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public abstract class AbstractTaskStepSummaryLine implements StepSummaryLine {

    private enum SummaryResult {
        OK("OK", "\u2705"),
        FAILED("FAILED", "\u274C"),
        ALREADY_APPLIED("ALREADY APPLIED", "\u23E9"),
        NOT_REACHED("NOT REACHED", "\u2754"),;

        private final String description;
        private final String icon;

        SummaryResult(String description, String icon) {
            this.description = description;
            this.icon = icon;
        }
    }

    protected static SummaryResult getResultFromSuccess(boolean success) {
        return success ? SummaryResult.OK : SummaryResult.FAILED;
    }

    private final String id;

    protected final SummaryResult result;

    protected AbstractTaskStepSummaryLine(String id, SummaryResult result) {
        this.id = id;
        this.result = result;
    }

    public String getId() {
        return id;
    }

    public String getPrettyResult() {
        return String.format("%s - %s", result.icon, result.description);
    }


    public static class InitialTaskSummaryLine extends AbstractTaskStepSummaryLine {

        private final TaskDescriptor desc;

        public InitialTaskSummaryLine(TaskDescriptor taskDescriptor) {
            super(taskDescriptor.getId(), null);
            this.desc = taskDescriptor;
        }

        @Override
        public String getPretty() {
            return desc.pretty();
        }

    }

    public static class StartedTaskSummaryLine extends AbstractTaskStepSummaryLine {

        public StartedTaskSummaryLine(ExecutableStep step) {
            super(step.getTask().getDescriptor().getId(), SummaryResult.OK);
        }


        @Override
        public String getPretty() {
            return String.format("\tStarted\t\t%s", getPrettyResult());
        }

    }

    public static class ExecutedTaskSummaryLine extends AbstractTaskStepSummaryLine {

        public ExecutedTaskSummaryLine(ExecutionStep step) {
            super(step.getTask().getDescriptor().getId(), getResultFromSuccess(step.isSuccessStep()));
        }


        @Override
        public String getPretty() {
            return String.format("\tExecution\t\t%s", getPrettyResult());
        }

    }

    public static class AfterExecutionTaskAuditSummaryLine extends AbstractTaskStepSummaryLine {


        public AfterExecutionTaskAuditSummaryLine(AfterExecutionAuditStep step) {
            super(step.getTask().getDescriptor().getId(), getResultFromSuccess(step.isSuccessStep()));
        }

        @Override
        public String getPretty() {
            return String.format("\tAudit execution\t%s", getPrettyResult());
        }

    }

    public static class RolledBackTaskSummaryLine extends AbstractTaskStepSummaryLine {

        public RolledBackTaskSummaryLine(RolledBackStep step) {
            super(step.getTask().getDescriptor().getId(), getResultFromSuccess(step.isSuccessStep()));
        }

        @Override
        public String getPretty() {
            return String.format("\tRolled back\t\t%s", getPrettyResult());
        }

    }

    public static class FailedCompletedManualRollbackTaskSummaryLine extends AbstractTaskStepSummaryLine {

        public FailedCompletedManualRollbackTaskSummaryLine(CompletedFailedManualRollback step) {
            super(step.getTask().getDescriptor().getId(), getResultFromSuccess(step.isSuccessStep()));
        }

        @Override
        public String getPretty() {
            return String.format("\tAudit rollback\t%s", getPrettyResult());
        }

    }

    public static class AlreadyAppliedTaskSummaryLine extends AbstractTaskStepSummaryLine {

        public AlreadyAppliedTaskSummaryLine(CompletedAlreadyAppliedStep step) {
            super(step.getTask().getDescriptor().getId(), SummaryResult.ALREADY_APPLIED);
        }

        @Override
        public String getPretty() {
            return String.format("\tExecution\t\t%s", getPrettyResult());
        }

    }

    public static class NotReachedTaskSummaryLine extends AbstractTaskStepSummaryLine {

        public NotReachedTaskSummaryLine(TaskDescriptor taskDescriptor) {
            super(taskDescriptor.getId(), SummaryResult.NOT_REACHED);
        }

        @Override
        public String getPretty() {
            return String.format("\tExecution\t\t%s", getPrettyResult());
        }

    }
}