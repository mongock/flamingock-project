package io.mongock.core.runtime.dependency;

import java.util.Optional;

public interface DependencyContext {

  <T> Optional<T> getBean(Class<T> type);

  Optional<Object> getBean(String name);
}
