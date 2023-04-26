package io.mongock.core.dependency;

import java.util.Optional;

public interface DependencyManager {

    Optional<Object> getDependency(Class<?> type, boolean lockGuarded);

    Optional<Object> getDependency(Class<?> type, String name, boolean lockGuarded);

    default Object getDependencyOrThrow(Class<?> type, String name, boolean lockGuarded) {
        return getDependency(type, name, lockGuarded)
                .orElseThrow(() -> new IllegalArgumentException(String.format("Wrong parameter[%s] with name: %s. Dependency not found", type, name)));
    }

}
