package io.flamingock.oss.core.execution.step.complete.failed;

import io.flamingock.oss.core.execution.step.rolledback.RolledBackStep;
import io.flamingock.oss.core.task.executable.ExecutableTask;
import io.flamingock.oss.core.util.Failed;
import io.flamingock.oss.core.execution.step.SuccessableStep;

public class CompleteAutoRolledBackStep extends RolledBackStep implements SuccessableStep, Failed {
    public CompleteAutoRolledBackStep(ExecutableTask task, boolean rollbackSuccess) {
        super(task, rollbackSuccess);
    }


}
