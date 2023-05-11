package io.mongock.core.execution.step.complete.failed;

import io.mongock.core.execution.step.SuccessableStep;
import io.mongock.core.execution.step.TaskStep;
import io.mongock.core.execution.step.rolledback.ManualRolledBackStep;
import io.mongock.core.task.executable.ExecutableTask;
import io.mongock.core.util.Failed;
import io.mongock.core.util.Result;

public class CompletedFailedManualRollback extends TaskStep implements SuccessableStep, Failed {

    public static CompletedFailedManualRollback fromRollbackAuditResult(ManualRolledBackStep rolledBack, Result auditResult) {
        return auditResult instanceof Result.Error
                ? new CompletedFailedAtRollbackAuditStep(rolledBack, ((Result.Error) auditResult).getError())
                : new CompletedFailedManualRollback(rolledBack.getTask());
    }

    protected CompletedFailedManualRollback(ExecutableTask task) {
        super(task);
    }

    @Override
    public boolean isSuccessStep() {
        return true;
    }


}
