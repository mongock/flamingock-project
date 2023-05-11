package io.mongock.core.execution.step.complete.failed;

import io.mongock.core.execution.step.SuccessableStep;
import io.mongock.core.execution.step.rolledback.RolledBackStep;
import io.mongock.core.task.executable.ExecutableTask;
import io.mongock.core.util.Failed;

public class CompleteAutoRolledBackStep extends RolledBackStep implements SuccessableStep, Failed {
    public CompleteAutoRolledBackStep(ExecutableTask task, boolean rollbackSuccess) {
        super(task, rollbackSuccess);
    }


}
