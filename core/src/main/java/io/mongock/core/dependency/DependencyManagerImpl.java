package io.mongock.core.dependency;

import java.util.Optional;

public class DependencyManagerImpl implements DependencyManager {
    @Override
    public Optional<Object> getDependency(Class<?> type, boolean lockGuarded) {
        return Optional.empty();
    }

    @Override
    public Optional<Object> getDependency(Class<?> type, String name, boolean lockGuarded) {
        return Optional.empty();
    }
}
