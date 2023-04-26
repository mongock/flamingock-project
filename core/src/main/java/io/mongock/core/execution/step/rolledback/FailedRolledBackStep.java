package io.mongock.core.execution.step.rolledback;

import io.mongock.core.task.executable.RollableTask;
import io.mongock.core.util.Failed;

public final class FailedRolledBackStep extends RolledBackStep implements Failed {

    private final Throwable error;


    public FailedRolledBackStep(RollableTask task, long duration, Throwable error) {
        super(task, false, duration);
        this.error = error;
    }

    @Override
    public Throwable getError() {
        return error;
    }

}
