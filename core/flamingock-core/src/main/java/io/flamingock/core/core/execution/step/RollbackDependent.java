package io.flamingock.core.core.execution.step;

import io.flamingock.core.core.execution.step.afteraudit.RollableStep;

import java.util.List;

public interface RollbackDependent {
    List<RollableStep> getRollbackDependents();
}
