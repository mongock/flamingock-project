package io.flamingock.core.core.execution.step.afteraudit;

import io.flamingock.core.core.execution.step.FailedStep;
import io.flamingock.core.core.execution.step.RollbackDependent;
import io.flamingock.core.core.execution.step.SuccessableStep;
import io.flamingock.core.core.task.executable.ExecutableTask;
import io.flamingock.core.core.task.executable.Rollback;
import io.flamingock.core.core.util.Result;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public abstract class FailedExecutionOrAuditStep extends AfterExecutionAuditStep
        implements SuccessableStep, FailedStep, RollbackDependent {


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

    public final Optional<RollableStep> getRollable() {
        return task.getRollback().map(RollableStep::new);
    }

    @Override
    public final List<RollableStep> getRollbackDependents() {
        return task.getDependentTasks()
                .stream()
                .map(RollableStep::new)
                .collect(Collectors.toList());
    }

}
