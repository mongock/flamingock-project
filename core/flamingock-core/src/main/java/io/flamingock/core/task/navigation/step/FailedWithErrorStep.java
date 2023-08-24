package io.flamingock.core.task.navigation.step;

public interface FailedWithErrorStep extends FailedStep {
    Throwable getError();
}
