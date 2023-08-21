package io.flamingock.core.core.execution.step.rolledback;

import io.flamingock.core.core.execution.step.FailedStep;
import io.flamingock.core.core.execution.step.SuccessableStep;
import io.flamingock.core.core.execution.step.complete.failed.CompletedFailedManualRollback;
import io.flamingock.core.core.task.executable.Rollback;
import io.flamingock.core.core.util.Result;

public class ManualRolledBackStep extends RolledBackStep implements SuccessableStep, FailedStep {

    private final long duration;

    protected ManualRolledBackStep(Rollback rollback, boolean rollbackSuccess, long duration) {
        super(rollback.getTask(), rollbackSuccess);
        this.duration = duration;
    }

    public static ManualRolledBackStep successfulRollback(Rollback rollback, long duration) {
        return new ManualRolledBackStep(rollback, true, duration);
    }

    public static ManualRolledBackStep failedRollback(Rollback rollback, long duration, Throwable error) {
        return new FailedManualRolledBackStep(rollback, duration, error);
    }

    public CompletedFailedManualRollback applyAuditResult(Result auditResult) {
        return CompletedFailedManualRollback.fromRollbackAuditResult(this, auditResult);
    }

    public long getDuration() {
        return duration;
    }

}
