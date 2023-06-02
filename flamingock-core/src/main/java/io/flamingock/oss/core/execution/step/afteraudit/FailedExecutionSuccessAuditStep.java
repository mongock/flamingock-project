package io.flamingock.oss.core.execution.step.afteraudit;

import io.flamingock.oss.core.task.executable.ExecutableTask;

public final class FailedExecutionSuccessAuditStep extends FailedExecutionOrAuditStep {
    FailedExecutionSuccessAuditStep(ExecutableTask task) {
        super(task, true);
    }
}
