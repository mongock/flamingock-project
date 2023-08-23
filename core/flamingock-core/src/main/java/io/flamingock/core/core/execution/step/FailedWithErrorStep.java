package io.flamingock.core.core.execution.step;

public interface FailedWithErrorStep extends FailedStep {
    Throwable getError();
}
