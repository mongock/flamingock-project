package io.mongock.core.runtime.dependency;

import java.util.Optional;

public interface Dependencymanager {

    Optional<Dependency> getDependency(Class<?> type);

    Optional<Dependency> getDependency(Class<?> type, String name);

    default Dependency getDependencyOrThrow(Class<?> type, String name) {
        return getDependency(type, name)
                .orElseThrow(() -> new IllegalArgumentException(String.format("Wrong parameter[%s] with name: %s. Dependency not found", type, name)));
    }

}
