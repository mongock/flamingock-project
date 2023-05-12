package io.mongock.core.execution.step.execution;

import io.mongock.core.execution.step.SuccessableStep;
import io.mongock.core.execution.step.TaskStep;
import io.mongock.core.execution.step.afteraudit.AfterExecutionAuditStep;
import io.mongock.core.task.executable.ExecutableTask;
import io.mongock.core.util.Result;

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
