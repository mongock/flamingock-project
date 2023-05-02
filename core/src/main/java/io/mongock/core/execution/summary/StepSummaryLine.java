package io.mongock.core.execution.summary;

import io.mongock.core.execution.step.afteraudit.AfterExecutionAuditStep;
import io.mongock.core.execution.step.complete.CompleteFailedStep;
import io.mongock.core.execution.step.execution.ExecutionStep;
import io.mongock.core.execution.step.rolledback.RolledBackStep;
import io.mongock.core.summary.SummaryLine;
import io.mongock.core.task.descriptor.TaskDescriptor;

import java.util.Objects;

public abstract class StepSummaryLine implements SummaryLine {

    private final String id;

    protected boolean success;

    private StepSummaryLine(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public boolean isSuccess() {
        return success;
    }

    protected String getStateFromSuccess() {
        return success ? "OK" : "FAILED";
    }


    public static class InitialSummaryLine extends StepSummaryLine {

        private final TaskDescriptor desc;

        public InitialSummaryLine(TaskDescriptor taskDescriptor) {
            super(taskDescriptor.getId());
            this.desc = taskDescriptor;
        }

        @Override
        public String getLine() {
            return ">" + desc.pretty();
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof InitialSummaryLine)) return false;
            InitialSummaryLine initial = (InitialSummaryLine) o;
            return desc.equals(initial.desc);
        }

        @Override
        public int hashCode() {
            return Objects.hash(desc);
        }


    }

    public static class ExecutedSummaryLine extends StepSummaryLine {

        public ExecutedSummaryLine(ExecutionStep step) {
            super(step.getTask().getDescriptor().getId());
            this.success = step.isSuccessStep();
        }


        @Override
        public String getLine() {
            return String.format("\tExecuted\t\t\t[%s]", getStateFromSuccess());
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof ExecutedSummaryLine)) return false;
            ExecutedSummaryLine executed = (ExecutedSummaryLine) o;
            return success == executed.success;
        }

        @Override
        public int hashCode() {
            return Objects.hash(success);
        }
    }

    public static class AfterExecutionAuditSummaryLine extends StepSummaryLine {


        public AfterExecutionAuditSummaryLine(AfterExecutionAuditStep step) {
            super(step.getTask().getDescriptor().getId());
            this.success = step.isSuccessStep();
        }

        @Override
        public String getLine() {
            return String.format("\tAudited execution\t[%s]", getStateFromSuccess());
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof AfterExecutionAuditSummaryLine)) return false;
            AfterExecutionAuditSummaryLine executed = (AfterExecutionAuditSummaryLine) o;
            return success == executed.success;
        }

        @Override
        public int hashCode() {
            return Objects.hash(success);
        }

    }

    public static class RolledBackSummaryLine extends StepSummaryLine {

        public RolledBackSummaryLine(RolledBackStep step) {
            super(step.getTask().getDescriptor().getId());
            this.success = step.isSuccessStep();
        }

        @Override
        public String getLine() {
            return String.format("\tRolled back\t\t\t\t[%s]", getStateFromSuccess());
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof RolledBackSummaryLine)) return false;
            RolledBackSummaryLine executed = (RolledBackSummaryLine) o;
            return success == executed.success;
        }

        @Override
        public int hashCode() {
            return Objects.hash(success);
        }


    }

    public static class FailedSummaryLine extends StepSummaryLine {

        public FailedSummaryLine(CompleteFailedStep step) {
            super(step.getTask().getDescriptor().getId());
            this.success = step.isSuccessStep();
        }

        @Override
        public String getLine() {
            return String.format("\tAudited rollback\t[%s]", getStateFromSuccess());
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof FailedSummaryLine)) return false;
            FailedSummaryLine executed = (FailedSummaryLine) o;
            return success == executed.success;
        }

        @Override
        public int hashCode() {
            return Objects.hash(success);
        }

    }
}