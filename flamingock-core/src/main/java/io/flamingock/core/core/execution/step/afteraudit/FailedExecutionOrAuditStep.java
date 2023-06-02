package io.flamingock.core.core.execution.step.afteraudit;

import io.flamingock.core.core.execution.step.SuccessableStep;
import io.flamingock.core.core.task.executable.ExecutableTask;
import io.flamingock.core.core.task.executable.RollableTask;
import io.flamingock.core.core.util.Result;

import java.util.Optional;

public abstract class FailedExecutionOrAuditStep extends AfterExecutionAuditStep implements SuccessableStep {


    public static FailedExecutionOrAuditStep instance(ExecutableTask task, Result auditResult) {
        if (auditResult instanceof Result.Error) {
            Result.Error errorResult = (Result.Error) auditResult;
            return new FailedAuditExecutionStep(task, errorResult.getError());
        } else {
            return new FailedExecutionSuccessAuditStep(task);
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
