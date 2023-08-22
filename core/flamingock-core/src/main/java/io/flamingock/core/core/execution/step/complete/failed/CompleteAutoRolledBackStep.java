package io.flamingock.core.core.execution.step.complete.failed;

import io.flamingock.core.core.execution.step.FailedStep;
import io.flamingock.core.core.execution.step.RollbackDependent;
import io.flamingock.core.core.execution.step.SuccessableStep;
import io.flamingock.core.core.execution.step.afteraudit.RollableStep;
import io.flamingock.core.core.execution.step.rolledback.RolledBackStep;
import io.flamingock.core.core.task.executable.ExecutableTask;

import java.util.List;
import java.util.stream.Collectors;

public class CompleteAutoRolledBackStep extends RolledBackStep implements SuccessableStep, FailedStep, RollbackDependent {
    public CompleteAutoRolledBackStep(ExecutableTask task, boolean rollbackSuccess) {
        super(task, rollbackSuccess);
    }


    @Override
    public List<RollableStep> getRollbackDependents() {
        return task.getDependentRollbacks()
                .stream()
                .skip(1)
                .map(RollableStep::new)
                .collect(Collectors.toList());
    }
}
