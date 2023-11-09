package io.flamingock.core.task.navigation.step.rolledback;

import io.flamingock.core.task.navigation.step.FailedWithErrorStep;
import io.flamingock.core.task.executable.Rollback;

public final class FailedManualRolledBackStep extends ManualRolledBackStep implements FailedWithErrorStep {

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
