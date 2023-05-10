package io.mongock.core.execution.step.afteraudit;

import io.mongock.core.task.executable.ExecutableTask;

public final class FailedExecutionSuccessAuditStep extends FailedExecutionOrAuditStep {
    FailedExecutionSuccessAuditStep(ExecutableTask task) {
        super(task, true);
    }
}
