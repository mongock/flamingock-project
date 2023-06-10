package io.flamingock.core.core.util;

public interface FailedWithError extends Failed {
    Throwable getError();
}
