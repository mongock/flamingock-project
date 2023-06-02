package io.flamingock.oss.core.util;

public interface FailedWithError extends Failed {
    Throwable getError();
}
