package io.flamingock.core.core.execution.step;

import io.flamingock.core.core.execution.step.afteraudit.RollableStep;

import java.util.List;

public interface RollableFailedStep extends FailedStep {
    List<RollableStep> getRollbackSteps();
}
