package io.flamingock.oss.core.execution.step.afteraudit;

import io.flamingock.oss.core.task.executable.ExecutableTask;
import io.flamingock.oss.core.util.FailedWithError;

public final class FailedAuditExecutionStep extends FailedExecutionOrAuditStep implements FailedWithError {

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
