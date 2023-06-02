package io.flamingock.oss.core.execution.step.rolledback;

import io.flamingock.oss.core.task.executable.RollableTask;
import io.flamingock.oss.core.util.FailedWithError;

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
