package io.flamingock.core.task.navigation.step.execution;

import io.flamingock.core.task.navigation.step.AbstractTaskStep;
import io.flamingock.core.task.navigation.step.SuccessableStep;
import io.flamingock.core.task.navigation.step.afteraudit.AfterExecutionAuditStep;
import io.flamingock.core.task.executable.ExecutableTask;
import io.flamingock.core.util.Result;

public abstract class ExecutionStep extends AbstractTaskStep implements SuccessableStep {

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
