package io.flamingock.core.core.execution.step.afteraudit;

import io.flamingock.core.core.task.executable.ExecutableTask;

public final class FailedExecutionSuccessAuditStep extends FailedExecutionOrAuditStep {
    FailedExecutionSuccessAuditStep(ExecutableTask task) {
        super(task, true);
    }
}
