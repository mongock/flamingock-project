package io.mongock.core.execution.step.afteraudit;

import io.mongock.core.task.executable.ExecutableTask;

public final class SuccessAuditFailedExecutionStep extends FailedExecutionOrAuditStep {
    SuccessAuditFailedExecutionStep(ExecutableTask task) {
        super(task, true);
    }
}
