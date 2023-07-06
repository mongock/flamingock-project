package io.flamingock.core.core.execution.step.complete.failed;

import io.flamingock.core.core.execution.step.SuccessableStep;
import io.flamingock.core.core.execution.step.rolledback.RolledBackStep;
import io.flamingock.core.core.task.executable.ExecutableTask;
import io.flamingock.core.core.execution.step.FailedStep;

public class CompleteAutoRolledBackStep extends RolledBackStep implements SuccessableStep, FailedStep {
    public CompleteAutoRolledBackStep(ExecutableTask task, boolean rollbackSuccess) {
        super(task, rollbackSuccess);
    }


}
