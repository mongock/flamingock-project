package io.mongock.core.execution.summary;

import io.mongock.core.execution.step.afteraudit.AfterExecutionAuditStep;
import io.mongock.core.execution.step.complete.AlreadyAppliedStep;
import io.mongock.core.execution.step.complete.CompleteFailedStep;
import io.mongock.core.execution.step.execution.ExecutionStep;
import io.mongock.core.execution.step.rolledback.RolledBackStep;
import io.mongock.core.summary.SummaryLine;
import io.mongock.core.task.descriptor.TaskDescriptor;

import java.util.Objects;

public abstract class StepSummaryLine implements SummaryLine {

    private enum SummaryResult {
        OK("OK"), FAILED("FAILED"), ALREADY_APPLIED("IGNORED - Already applied");

        private String value;
        SummaryResult(String value) {
            this.value = value;
        }
    }

    private final String id;

    protected SummaryResult result;

    private StepSummaryLine(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public String getPrettyResult() {
        return result.value;
    }

    protected void setResultFromSuccess(boolean success) {
        result = success ? SummaryResult.OK : SummaryResult.FAILED;
    }


    public static class InitialSummaryLine extends StepSummaryLine {

        private final TaskDescriptor desc;

        public InitialSummaryLine(TaskDescriptor taskDescriptor) {
            super(taskDescriptor.getId());
            this.desc = taskDescriptor;
        }

        @Override
        public String getLine() {
            return ">> " + desc.pretty();
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
            setResultFromSuccess(step.isSuccessStep());
        }


        @Override
        public String getLine() {
            return String.format("\tExecution\t\t[%s]", getPrettyResult());
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof ExecutedSummaryLine)) return false;
            ExecutedSummaryLine executed = (ExecutedSummaryLine) o;
            return result == executed.result;
        }

        @Override
        public int hashCode() {
            return Objects.hash(result);
        }
    }

    public static class AfterExecutionAuditSummaryLine extends StepSummaryLine {


        public AfterExecutionAuditSummaryLine(AfterExecutionAuditStep step) {
            super(step.getTask().getDescriptor().getId());
            setResultFromSuccess(step.isSuccessStep());
        }

        @Override
        public String getLine() {
            return String.format("\tAudit execution\t[%s]", getPrettyResult());
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof AfterExecutionAuditSummaryLine)) return false;
            AfterExecutionAuditSummaryLine executed = (AfterExecutionAuditSummaryLine) o;
            return result == executed.result;
        }

        @Override
        public int hashCode() {
            return Objects.hash(result);
        }

    }

    public static class RolledBackSummaryLine extends StepSummaryLine {

        public RolledBackSummaryLine(RolledBackStep step) {
            super(step.getTask().getDescriptor().getId());
            setResultFromSuccess(step.isSuccessStep());
        }

        @Override
        public String getLine() {
            return String.format("\tRolled back\t\t[%s]", getPrettyResult());
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof RolledBackSummaryLine)) return false;
            RolledBackSummaryLine executed = (RolledBackSummaryLine) o;
            return result == executed.result;
        }

        @Override
        public int hashCode() {
            return Objects.hash(result);
        }


    }

    public static class FailedSummaryLine extends StepSummaryLine {

        public FailedSummaryLine(CompleteFailedStep step) {
            super(step.getTask().getDescriptor().getId());
            setResultFromSuccess(step.isSuccessStep());
        }

        @Override
        public String getLine() {
            return String.format("\tAudit rollback\t[%s]", getPrettyResult());
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof FailedSummaryLine)) return false;
            FailedSummaryLine executed = (FailedSummaryLine) o;
            return result == executed.result;
        }

        @Override
        public int hashCode() {
            return Objects.hash(result);
        }

    }


    public static class AlreadyAppliedSummaryLine extends StepSummaryLine {

        public AlreadyAppliedSummaryLine(AlreadyAppliedStep step) {
            super(step.getTask().getDescriptor().getId());
            this.result = SummaryResult.ALREADY_APPLIED;
        }

        @Override
        public String getLine() {
            return String.format("\tExecution\t\t[%s]", getPrettyResult());
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof FailedSummaryLine)) return false;
            FailedSummaryLine executed = (FailedSummaryLine) o;
            return result == executed.result;
        }

        @Override
        public int hashCode() {
            return Objects.hash(result);
        }

    }
}