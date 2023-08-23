package io.flamingock.core.core.execution.step.afteraudit;

import io.flamingock.core.core.execution.step.FailedWithErrorStep;
import io.flamingock.core.core.task.executable.ExecutableTask;

public final class FailedAuditExecutionStep extends FailedExecutionOrAuditStep implements FailedWithErrorStep {

    private final Throwable error;

    FailedAuditExecutionStep(ExecutableTask task, Throwable error) {
        super(task, false);
        this.error = error;
    }

    @Override
    public Throwable getError() {
        return error;
    }


}
