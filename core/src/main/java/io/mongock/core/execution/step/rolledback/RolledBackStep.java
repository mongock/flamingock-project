package io.mongock.core.execution.step.rolledback;

import io.mongock.core.util.Result;
import io.mongock.core.execution.step.SuccessableStep;
import io.mongock.core.execution.step.TaskStep;
import io.mongock.core.execution.step.complete.CompleteFailedStep;
import io.mongock.core.task.executable.RollableTask;

public abstract class RolledBackStep extends TaskStep implements SuccessableStep {


    private final RollableTask rollableTask;

    private final boolean rollbackSuccess;

    private final long duration;

    protected RolledBackStep(RollableTask task, boolean rollbackSuccess, long duration) {
        super(task);
        this.rollableTask = task;
        this.rollbackSuccess = rollbackSuccess;
        this.duration = duration;
    }


    @Override
    public RollableTask getTask() {
        return rollableTask;
    }


    public CompleteFailedStep applyAuditResult(Result auditResult) {
        return CompleteFailedStep.fromRollbackAuditResult(this, auditResult);
    }

    public long getDuration() {
        return duration;
    }

    @Override
    public final boolean isSuccessStep() {
        return rollbackSuccess;
    }
}
