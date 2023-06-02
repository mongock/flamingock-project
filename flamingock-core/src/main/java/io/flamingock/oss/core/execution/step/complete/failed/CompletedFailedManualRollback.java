package io.flamingock.oss.core.execution.step.complete.failed;

import io.flamingock.oss.core.execution.step.rolledback.ManualRolledBackStep;
import io.flamingock.oss.core.task.executable.ExecutableTask;
import io.flamingock.oss.core.util.Failed;
import io.flamingock.oss.core.util.Result;
import io.flamingock.oss.core.execution.step.SuccessableStep;
import io.flamingock.oss.core.execution.step.TaskStep;

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
