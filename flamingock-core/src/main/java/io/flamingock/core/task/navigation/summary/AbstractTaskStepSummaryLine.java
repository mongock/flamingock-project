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
import io.flamingock.core.task.navigation.step.afteraudit.AfterExecutionAuditStep;
import io.flamingock.core.task.navigation.step.complete.CompletedAlreadyAppliedStep;
import io.flamingock.core.task.navigation.step.complete.failed.CompletedFailedManualRollback;
import io.flamingock.core.task.navigation.step.execution.ExecutionStep;
import io.flamingock.core.task.navigation.step.rolledback.RolledBackStep;

import java.util.Objects;

public abstract class AbstractTaskStepSummaryLine implements StepSummaryLine {

    private enum SummaryResult {
        OK("OK", "\u2705"), FAILED("FAILED", "\u274C"), ALREADY_APPLIED("IGNORED - Already applied", "\u23ED");

        private final String description;
        private final String icon;

        SummaryResult(String description, String icon) {
            this.description = description;
            this.icon = icon;
        }
    }

    private final String id;

    protected SummaryResult result;

    protected AbstractTaskStepSummaryLine(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public String getPrettyResult() {
        return String.format("%s - %s", result.icon, result.description);
    }

    protected void setResultFromSuccess(boolean success) {
        result = success ? SummaryResult.OK : SummaryResult.FAILED;
    }

    public static class InitialTaskSummaryLine extends AbstractTaskStepSummaryLine {

        private final TaskDescriptor desc;

        public InitialTaskSummaryLine(TaskDescriptor taskDescriptor) {
            super(taskDescriptor.getId());
            this.desc = taskDescriptor;
        }

        @Override
        public String getPretty() {
            return desc.pretty();
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof InitialTaskSummaryLine)) return false;
            InitialTaskSummaryLine initial = (InitialTaskSummaryLine) o;
            return desc.equals(initial.desc);
        }

        @Override
        public int hashCode() {
            return Objects.hash(desc);
        }


    }

    public static class ExecutedTaskSummaryLine extends AbstractTaskStepSummaryLine {

        public ExecutedTaskSummaryLine(ExecutionStep step) {
            super(step.getTask().getDescriptor().getId());
            setResultFromSuccess(step.isSuccessStep());
        }


        @Override
        public String getPretty() {
            return String.format("\tExecution\t\t%s", getPrettyResult());
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof ExecutedTaskSummaryLine)) return false;
            ExecutedTaskSummaryLine executed = (ExecutedTaskSummaryLine) o;
            return result == executed.result;
        }

        @Override
        public int hashCode() {
            return Objects.hash(result);
        }
    }

    public static class AfterExecutionTaskAuditSummaryLine extends AbstractTaskStepSummaryLine {


        public AfterExecutionTaskAuditSummaryLine(AfterExecutionAuditStep step) {
            super(step.getTask().getDescriptor().getId());
            setResultFromSuccess(step.isSuccessStep());
        }

        @Override
        public String getPretty() {
            return String.format("\tAudit execution\t%s", getPrettyResult());
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof AfterExecutionTaskAuditSummaryLine)) return false;
            AfterExecutionTaskAuditSummaryLine executed = (AfterExecutionTaskAuditSummaryLine) o;
            return result == executed.result;
        }

        @Override
        public int hashCode() {
            return Objects.hash(result);
        }

    }

    public static class RolledBackTaskSummaryLine extends AbstractTaskStepSummaryLine {

        public RolledBackTaskSummaryLine(RolledBackStep step) {
            super(step.getTask().getDescriptor().getId());
            setResultFromSuccess(step.isSuccessStep());
        }

        @Override
        public String getPretty() {
            return String.format("\tRolled back\t\t%s", getPrettyResult());
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof RolledBackTaskSummaryLine)) return false;
            RolledBackTaskSummaryLine executed = (RolledBackTaskSummaryLine) o;
            return result == executed.result;
        }

        @Override
        public int hashCode() {
            return Objects.hash(result);
        }


    }

    public static class FailedCompletedManualRollbackTaskSummaryLine extends AbstractTaskStepSummaryLine {

        public FailedCompletedManualRollbackTaskSummaryLine(CompletedFailedManualRollback step) {
            super(step.getTask().getDescriptor().getId());
            setResultFromSuccess(step.isSuccessStep());
        }

        @Override
        public String getPretty() {
            return String.format("\tAudit rollback\t%s", getPrettyResult());
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof FailedCompletedManualRollbackTaskSummaryLine)) return false;
            FailedCompletedManualRollbackTaskSummaryLine executed = (FailedCompletedManualRollbackTaskSummaryLine) o;
            return result == executed.result;
        }

        @Override
        public int hashCode() {
            return Objects.hash(result);
        }

    }

    public static class AlreadyAppliedTaskSummaryLine extends AbstractTaskStepSummaryLine {

        public AlreadyAppliedTaskSummaryLine(CompletedAlreadyAppliedStep step) {
            super(step.getTask().getDescriptor().getId());
            this.result = SummaryResult.ALREADY_APPLIED;
        }

        @Override
        public String getPretty() {
            return String.format("\tExecution\t\t%s", getPrettyResult());
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof FailedCompletedManualRollbackTaskSummaryLine)) return false;
            FailedCompletedManualRollbackTaskSummaryLine executed = (FailedCompletedManualRollbackTaskSummaryLine) o;
            return result == executed.result;
        }

        @Override
        public int hashCode() {
            return Objects.hash(result);
        }

    }
}