package io.flamingock.core.core.execution.step.afteraudit;

import io.flamingock.core.core.execution.step.AbstractTaskStep;
import io.flamingock.core.core.execution.step.SuccessableStep;
import io.flamingock.core.core.task.executable.ExecutableTask;

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
