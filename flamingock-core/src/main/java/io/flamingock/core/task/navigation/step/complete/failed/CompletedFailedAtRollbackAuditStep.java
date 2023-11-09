package io.flamingock.core.task.navigation.step.complete.failed;

import io.flamingock.core.task.navigation.step.FailedWithErrorStep;
import io.flamingock.core.task.navigation.step.rolledback.ManualRolledBackStep;

public final class CompletedFailedAtRollbackAuditStep extends CompletedFailedManualRollback implements FailedWithErrorStep {

    private final Throwable errorAtRollbackAudit;

    CompletedFailedAtRollbackAuditStep(ManualRolledBackStep taskStep, Throwable errorAtRollbackAudit) {
        super(taskStep.getTask());
        this.errorAtRollbackAudit = errorAtRollbackAudit;
    }

    @Override
    public Throwable getError() {
        return errorAtRollbackAudit;
    }

    @Override
    public boolean isSuccessStep() {
        return false;
    }

}
