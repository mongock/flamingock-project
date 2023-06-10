package io.flamingock.core.core.execution.step.complete.failed;

import io.flamingock.core.core.execution.step.rolledback.ManualRolledBackStep;
import io.flamingock.core.core.util.FailedWithError;

public final class CompletedFailedAtRollbackAuditStep extends CompletedFailedManualRollback implements FailedWithError {

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
