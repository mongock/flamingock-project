package io.flamingock.core.core.audit.writer;

import io.flamingock.core.core.execution.step.FailedStepWithError;
import io.flamingock.core.core.execution.step.TaskStep;
import io.flamingock.core.core.execution.step.execution.ExecutionStep;
import io.flamingock.core.core.execution.step.rolledback.ManualRolledBackStep;

import java.time.LocalDateTime;
import java.util.Optional;

public final class RuntimeContext {

    public static Builder builder() {
        return new Builder();
    }

    private enum ExecutionResult {SUCCESS, FAILED}

    private final ExecutionResult executionResult;
    private final long duration;

    private final LocalDateTime executedAt;

    private final String methodExecutor;

    private final Throwable error;

    private RuntimeContext(ExecutionResult executionResult,
                           long duration,
                           LocalDateTime executedAt,
                           String methodExecutor,
                           Throwable error) {
        this.executionResult = executionResult;
        this.duration = duration;
        this.executedAt = executedAt;
        this.methodExecutor = methodExecutor;
        this.error = error;
    }

    public long getDuration() {
        return duration;
    }

    public LocalDateTime getExecutedAt() {
        return executedAt;
    }

    public String getMethodExecutor() {
        return methodExecutor;
    }

    public Optional<Throwable> getError() {
        return Optional.ofNullable(error);
    }

    public boolean isSuccess() {
        return executionResult == ExecutionResult.SUCCESS;
    }

    public boolean isFailed() {
        return executionResult == ExecutionResult.FAILED;
    }


    public static class Builder {

        private ExecutionResult executionResult = ExecutionResult.SUCCESS;
        private long duration = -1L;

        private LocalDateTime executedAt;

        private String methodExecutor;

        private Throwable error;

        private Builder() {
        }

        public Builder setTaskStep(ExecutionStep taskStep) {
            setTaskStepPrivate(taskStep);
            return this;
        }

        private void setTaskStepPrivate(ExecutionStep executedStep) {
            duration = executedStep.getDuration();
            methodExecutor = executedStep.getTask().getExecutionMethodName();
            setFailure(executedStep);
        }

        public Builder setTaskStep(ManualRolledBackStep rolledBackStep) {
            duration = rolledBackStep.getDuration();
            methodExecutor = rolledBackStep.getTask().getExecutionMethodName();
            setFailure(rolledBackStep);
            return this;
        }

        private void setFailure(TaskStep taskStep) {
            if (taskStep instanceof FailedStepWithError) {
                executionResult = ExecutionResult.FAILED;
                error = ((FailedStepWithError) taskStep).getError();
            } else {
                executionResult = ExecutionResult.SUCCESS;
                error = null;
            }
        }


        public Builder setExecutedAt(LocalDateTime executedAt) {
            this.executedAt = executedAt;
            return this;
        }

        public RuntimeContext build() {
            if (methodExecutor == null) {
                throw new IllegalArgumentException("[methodExecutor] cannot be null when building RuntimeContext");
            }
            if (executedAt == null) {
                throw new IllegalArgumentException("[executedAt] cannot be null when building RuntimeContext");
            }
            return new RuntimeContext(executionResult, duration, executedAt, methodExecutor, error);

        }
    }
}
