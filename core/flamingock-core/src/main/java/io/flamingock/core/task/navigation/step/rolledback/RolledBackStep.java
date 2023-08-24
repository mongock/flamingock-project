package io.flamingock.core.task.navigation.step.rolledback;

import io.flamingock.core.task.navigation.step.AbstractTaskStep;
import io.flamingock.core.task.navigation.step.FailedStep;
import io.flamingock.core.task.navigation.step.SuccessableStep;
import io.flamingock.core.task.executable.ExecutableTask;

public abstract class RolledBackStep extends AbstractTaskStep implements SuccessableStep, FailedStep {

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
