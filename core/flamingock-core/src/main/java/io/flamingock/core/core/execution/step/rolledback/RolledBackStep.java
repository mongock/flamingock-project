package io.flamingock.core.core.execution.step.rolledback;

import io.flamingock.core.core.execution.step.SuccessableStep;
import io.flamingock.core.core.execution.step.AbstractTaskStep;
import io.flamingock.core.core.task.executable.OrderedExecutableTask;
import io.flamingock.core.core.execution.step.FailedStep;

public abstract class RolledBackStep extends AbstractTaskStep implements SuccessableStep, FailedStep {

    private final boolean rollbackSuccess;


    public RolledBackStep(OrderedExecutableTask task, boolean rollbackSuccess) {
        super(task);
        this.rollbackSuccess = rollbackSuccess;
    }


    @Override
    public final boolean isSuccessStep() {
        return rollbackSuccess;
    }
}
