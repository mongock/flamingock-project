package io.flamingock.core.core.execution.step.afteraudit;

import io.flamingock.core.core.task.executable.OrderedExecutableTask;

public final class FailedExecutionSuccessAuditStep extends FailedExecutionOrAuditStep {
    FailedExecutionSuccessAuditStep(OrderedExecutableTask task) {
        super(task, true);
    }
}
