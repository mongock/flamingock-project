package io.flamingock.core.core.execution.step.rolledback;

import io.flamingock.core.core.execution.step.FailedStepWithError;
import io.flamingock.core.core.task.executable.Rollback;

public final class FailedManualRolledBackStep extends ManualRolledBackStep implements FailedStepWithError {

    private final Throwable error;


    FailedManualRolledBackStep(Rollback rollback, long duration, Throwable error) {
        super(rollback, false, duration);
        this.error = error;
    }

    @Override
    public Throwable getError() {
        return error;
    }

}
