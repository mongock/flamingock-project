package io.mongock.core.execution.step.rolledback;

import io.mongock.core.task.executable.RollableTask;

public final class SuccessRolledBackStep extends RolledBackStep {

    public SuccessRolledBackStep(RollableTask task, long duration) {
        super(task, true, duration);

    }

}
