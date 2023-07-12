package io.flamingock.core.core.execution.step;

public interface FailedStepWithError extends FailedStep {
    Throwable getError();
}
