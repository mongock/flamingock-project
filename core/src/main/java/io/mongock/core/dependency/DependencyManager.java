package io.mongock.core.dependency;

import java.util.Optional;

public interface DependencyManager {

    Optional<Object> getDependency(Class<?> type);

    Optional<Object> getDependency(Class<?> type, String name);

    default Object getDependencyOrThrow(Class<?> type, String name) {
        return getDependency(type, name)
                .orElseThrow(() -> new IllegalArgumentException(String.format("Wrong parameter[%s] with name: %s. Dependency not found", type, name)));
    }

}
