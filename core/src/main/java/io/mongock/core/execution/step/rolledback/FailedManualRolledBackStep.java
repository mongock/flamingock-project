package io.mongock.core.execution.step.rolledback;

import io.mongock.core.task.executable.RollableTask;
import io.mongock.core.util.FailedWithError;

public final class FailedManualRolledBackStep extends ManualRolledBackStep implements FailedWithError {

    private final Throwable error;


    FailedManualRolledBackStep(RollableTask task, long duration, Throwable error) {
        super(task, false, duration);
        this.error = error;
    }

    @Override
    public Throwable getError() {
        return error;
    }

}
