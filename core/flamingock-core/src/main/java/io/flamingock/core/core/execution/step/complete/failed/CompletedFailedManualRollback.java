package io.flamingock.core.core.execution.step.complete.failed;

import io.flamingock.core.core.execution.step.SuccessableStep;
import io.flamingock.core.core.execution.step.TaskStep;
import io.flamingock.core.core.execution.step.rolledback.ManualRolledBackStep;
import io.flamingock.core.core.task.executable.ExecutableTask;
import io.flamingock.core.core.util.Failed;
import io.flamingock.core.core.util.Result;

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
