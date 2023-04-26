package io.mongock.core.execution.step.afteraudit;

import io.mongock.core.audit.domain.AuditResult;
import io.mongock.core.execution.step.SuccessableStep;
import io.mongock.core.task.executable.ExecutableTask;
import io.mongock.core.task.executable.RollableTask;

import java.util.Optional;

/**
 * TODO find an appropriate name for this class
 */
public abstract class FailedExecutionOrAuditStep extends AfterExecutionAuditStep implements SuccessableStep {


    public static FailedExecutionOrAuditStep instance(ExecutableTask task, AuditResult auditResult) {
        if (auditResult instanceof AuditResult.Error) {
            AuditResult.Error errorResult = (AuditResult.Error) auditResult;
            return new FailedAuditUnknownExecutionStep(task, errorResult.getError());
        } else {
            return new SuccessAuditFailedExecutionStep(task);
        }
    }

    protected FailedExecutionOrAuditStep(ExecutableTask task, boolean successExecutionAudit) {
        super(task, successExecutionAudit);
    }

    public final Optional<RollableStep> getRollableIfPresent() {
        return task instanceof RollableTask
                ? Optional.of(new RollableStep((RollableTask) task))
                : Optional.empty();
    }

}
