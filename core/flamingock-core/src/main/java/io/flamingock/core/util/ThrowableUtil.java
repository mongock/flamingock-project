package io.flamingock.core.util;

public final class ThrowableUtil {
    private ThrowableUtil() {
    }

    public static String serialize(Throwable throwable) {
        return throwable != null ? throwable.getMessage() : "";
    }
}
