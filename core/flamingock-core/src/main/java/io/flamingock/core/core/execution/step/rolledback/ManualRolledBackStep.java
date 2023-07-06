package io.flamingock.core.core.execution.step.rolledback;

import io.flamingock.core.core.execution.step.SuccessableStep;
import io.flamingock.core.core.execution.step.complete.failed.CompletedFailedManualRollback;
import io.flamingock.core.core.task.executable.RollableTask;
import io.flamingock.core.core.execution.step.FailedStep;
import io.flamingock.core.core.util.Result;

public class ManualRolledBackStep extends RolledBackStep implements SuccessableStep, FailedStep {

    public static ManualRolledBackStep successfulRollback(RollableTask task, long duration) {
        return new ManualRolledBackStep(task, true, duration);
    }

    public static ManualRolledBackStep failedRollback(RollableTask task, long duration, Throwable error) {
        return new FailedManualRolledBackStep(task, duration, error);
    }

    private final RollableTask rollableTask;

    private final long duration;

    protected ManualRolledBackStep(RollableTask task, boolean rollbackSuccess, long duration) {
        super(task, rollbackSuccess);
        this.rollableTask = task;
        this.duration = duration;
    }


    @Override
    public RollableTask getTask() {
        return rollableTask;
    }


    public CompletedFailedManualRollback applyAuditResult(Result auditResult) {
        return CompletedFailedManualRollback.fromRollbackAuditResult(this, auditResult);
    }

    public long getDuration() {
        return duration;
    }

}
