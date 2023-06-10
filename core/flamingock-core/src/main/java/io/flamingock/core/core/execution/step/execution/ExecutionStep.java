package io.flamingock.core.core.execution.step.execution;

import io.flamingock.core.core.execution.step.SuccessableStep;
import io.flamingock.core.core.execution.step.TaskStep;
import io.flamingock.core.core.execution.step.afteraudit.AfterExecutionAuditStep;
import io.flamingock.core.core.task.executable.ExecutableTask;
import io.flamingock.core.core.util.Result;

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
