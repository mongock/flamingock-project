package io.mongock.core.execution.step.complete;

import io.mongock.core.audit.domain.AuditResult;
import io.mongock.core.execution.step.SuccessableStep;
import io.mongock.core.execution.step.TaskStep;
import io.mongock.core.execution.step.rolledback.RolledBackStep;
import io.mongock.core.task.executable.ExecutableTask;

public class CompleteFailedStep extends TaskStep implements SuccessableStep {

    private final boolean rollbackAuditSuccess;

    public static CompleteFailedStep fromRollbackAuditResult(RolledBackStep rolledBack, AuditResult auditResult) {
        return auditResult instanceof AuditResult.Error
                ? new CompleteFailedAuditFailedStep(rolledBack, ((AuditResult.Error) auditResult).getError())
                : new CompleteFailedStep(rolledBack.getTask(), true);
    }

    CompleteFailedStep(ExecutableTask task, boolean rollbackAuditSuccess) {
        super(task);
        this.rollbackAuditSuccess = rollbackAuditSuccess;
    }

    @Override
    public final boolean isSuccessStep() {
        return rollbackAuditSuccess;
    }


}
