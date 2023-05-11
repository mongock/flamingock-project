package io.mongock.core.execution.step.rolledback;

import io.mongock.core.execution.step.SuccessableStep;
import io.mongock.core.execution.step.TaskStep;
import io.mongock.core.task.executable.ExecutableTask;
import io.mongock.core.util.Failed;

public abstract class RolledBackStep extends TaskStep implements SuccessableStep, Failed {

    private final boolean rollbackSuccess;


    public RolledBackStep(ExecutableTask task, boolean rollbackSuccess) {
        super(task);
        this.rollbackSuccess = rollbackSuccess;
    }


    @Override
    public final boolean isSuccessStep() {
        return rollbackSuccess;
    }
}
