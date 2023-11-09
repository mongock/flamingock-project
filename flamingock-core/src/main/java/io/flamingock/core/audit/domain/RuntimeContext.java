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

package io.flamingock.core.audit.domain;

import io.flamingock.core.task.navigation.step.FailedWithErrorStep;
import io.flamingock.core.task.navigation.step.TaskStep;
import io.flamingock.core.task.navigation.step.execution.ExecutionStep;
import io.flamingock.core.task.navigation.step.rolledback.ManualRolledBackStep;

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
            duration = taskStep.getDuration();
            methodExecutor = taskStep.getTask().getExecutionMethodName();
            setFailure(taskStep);
            return this;
        }

        public Builder setTaskStep(ManualRolledBackStep rolledBackStep) {
            duration = rolledBackStep.getDuration();
            methodExecutor = rolledBackStep.getRollback().getRollbackMethodName();
            setFailure(rolledBackStep);
            return this;
        }

        private void setFailure(TaskStep taskStep) {
            if (taskStep instanceof FailedWithErrorStep) {
                executionResult = ExecutionResult.FAILED;
                error = ((FailedWithErrorStep) taskStep).getError();
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
