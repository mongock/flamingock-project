package io.flamingock.core.core.util;

import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Stream;

public final class StreamUtil {

    public static <T> Optional<T> processUntil(Stream<T> stream, Predicate<T> predicate) {
        return stream.filter(predicate).findFirst();
    }
}
