package io.flamingock.core.task.navigation.step.afteraudit;

import io.flamingock.core.task.navigation.step.AbstractTaskStep;
import io.flamingock.core.task.navigation.step.SuccessableStep;
import io.flamingock.core.task.executable.ExecutableTask;

public abstract class AfterExecutionAuditStep extends AbstractTaskStep implements SuccessableStep {
    protected final boolean successExecutionAudit;

    protected AfterExecutionAuditStep(ExecutableTask task, boolean successExecutionAudit) {
        super(task);
        this.successExecutionAudit = successExecutionAudit;
    }

    @Override
    public final boolean isSuccessStep() {
        return successExecutionAudit;
    }

}
