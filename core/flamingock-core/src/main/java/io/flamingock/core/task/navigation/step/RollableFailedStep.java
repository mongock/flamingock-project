package io.flamingock.core.task.navigation.step;

import io.flamingock.core.task.navigation.step.afteraudit.RollableStep;

import java.util.List;

public interface RollableFailedStep extends FailedStep {
    List<RollableStep> getRollbackSteps();
}
