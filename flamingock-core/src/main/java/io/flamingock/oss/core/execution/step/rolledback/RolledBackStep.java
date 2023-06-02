package io.flamingock.oss.core.execution.step.rolledback;

import io.flamingock.oss.core.task.executable.ExecutableTask;
import io.flamingock.oss.core.util.Failed;
import io.flamingock.oss.core.execution.step.SuccessableStep;
import io.flamingock.oss.core.execution.step.TaskStep;

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
