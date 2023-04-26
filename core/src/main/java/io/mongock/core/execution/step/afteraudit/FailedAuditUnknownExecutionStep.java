package io.mongock.core.execution.step.afteraudit;

import io.mongock.core.task.executable.ExecutableTask;
import io.mongock.core.util.Failed;

public final class FailedAuditUnknownExecutionStep extends FailedExecutionOrAuditStep implements Failed {

    private final Throwable error;

    FailedAuditUnknownExecutionStep(ExecutableTask task, Throwable error) {
        super(task, false);
        this.error = error;
    }

    @Override
    public Throwable getError() {
        return error;
    }


}
