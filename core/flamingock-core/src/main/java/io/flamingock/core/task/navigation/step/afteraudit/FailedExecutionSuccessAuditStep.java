package io.flamingock.core.task.navigation.step.afteraudit;

import io.flamingock.core.task.executable.ExecutableTask;

public final class FailedExecutionSuccessAuditStep extends FailedExecutionOrAuditStep {
    FailedExecutionSuccessAuditStep(ExecutableTask task) {
        super(task, true);
    }
}
