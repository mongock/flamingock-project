package io.mongock.core.util;

public interface FailedWithError extends Failed {
    Throwable getError();
}
