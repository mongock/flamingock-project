package io.flamingock.core.task.navigation.step.complete.failed;

import io.flamingock.core.task.navigation.step.AbstractTaskStep;
import io.flamingock.core.task.navigation.step.FailedStep;
import io.flamingock.core.task.navigation.step.SuccessableStep;
import io.flamingock.core.task.navigation.step.rolledback.ManualRolledBackStep;
import io.flamingock.core.task.executable.ExecutableTask;
import io.flamingock.core.util.Result;

public class CompletedFailedManualRollback extends AbstractTaskStep implements SuccessableStep, FailedStep {

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
