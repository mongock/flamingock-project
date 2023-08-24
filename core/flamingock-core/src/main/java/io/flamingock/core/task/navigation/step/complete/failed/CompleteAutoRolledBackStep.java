package io.flamingock.core.task.navigation.step.complete.failed;

import io.flamingock.core.task.navigation.step.RollableFailedStep;
import io.flamingock.core.task.navigation.step.SuccessableStep;
import io.flamingock.core.task.navigation.step.afteraudit.RollableStep;
import io.flamingock.core.task.navigation.step.rolledback.RolledBackStep;
import io.flamingock.core.task.executable.ExecutableTask;

import java.util.List;
import java.util.stream.Collectors;

public class CompleteAutoRolledBackStep extends RolledBackStep implements SuccessableStep, RollableFailedStep {
    public CompleteAutoRolledBackStep(ExecutableTask task, boolean rollbackSuccess) {
        super(task, rollbackSuccess);
    }


    @Override
    public List<RollableStep> getRollbackSteps() {
        return task.getRollbackChain()
                .stream()
                .skip(1)//Skips the first one(its own rollback), because it's AutoRollback
                .map(RollableStep::new)
                .collect(Collectors.toList());
    }
}
