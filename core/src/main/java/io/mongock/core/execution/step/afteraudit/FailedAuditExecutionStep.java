package io.mongock.core.execution.step.afteraudit;

import io.mongock.core.task.executable.ExecutableTask;
import io.mongock.core.util.FailedWithError;

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
