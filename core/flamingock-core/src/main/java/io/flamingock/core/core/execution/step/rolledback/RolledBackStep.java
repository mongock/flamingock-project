package io.flamingock.core.core.execution.step.rolledback;

import io.flamingock.core.core.execution.step.SuccessableStep;
import io.flamingock.core.core.execution.step.TaskStep;
import io.flamingock.core.core.task.executable.ExecutableTask;
import io.flamingock.core.core.util.Failed;

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
