package io.flamingock.core.runtime.dependency;

import java.util.Optional;

public interface PropertyDependencyResolver {

    Optional<String> getProperty(String key);

    <T> Optional<T> getPropertyAs(String key, Class<T> type);
}
