package io.flamingock.oss.core.execution.step.afteraudit;

import io.flamingock.oss.core.task.executable.ExecutableTask;
import io.flamingock.oss.core.execution.step.SuccessableStep;
import io.flamingock.oss.core.execution.step.TaskStep;

public abstract class AfterExecutionAuditStep extends TaskStep implements SuccessableStep {
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
