package io.flamingock.core.task.navigation.step.afteraudit;

import io.flamingock.core.task.navigation.step.FailedWithErrorStep;
import io.flamingock.core.task.executable.ExecutableTask;

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
