package io.flamingock.oss.core.execution.step.execution;

import io.flamingock.oss.core.execution.step.afteraudit.AfterExecutionAuditStep;
import io.flamingock.oss.core.task.executable.ExecutableTask;
import io.flamingock.oss.core.util.Result;
import io.flamingock.oss.core.execution.step.SuccessableStep;
import io.flamingock.oss.core.execution.step.TaskStep;

public abstract class ExecutionStep extends TaskStep implements SuccessableStep {

    private final long duration;
    private final boolean successExecution;

    protected ExecutionStep(ExecutableTask task, boolean successExecution, long duration) {
        super(task);
        this.successExecution = successExecution;
        this.duration = duration;
    }


    public long getDuration() {
        return duration;
    }

    public abstract AfterExecutionAuditStep applyAuditResult(Result saveResult);

    @Override
    public final boolean isSuccessStep() {
        return successExecution;
    }

}
