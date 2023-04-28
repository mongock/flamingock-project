package io.mongock.core.execution.step.afteraudit;

import io.mongock.core.execution.step.SuccessableStep;
import io.mongock.core.execution.step.TaskStep;
import io.mongock.core.task.executable.ExecutableTask;

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
