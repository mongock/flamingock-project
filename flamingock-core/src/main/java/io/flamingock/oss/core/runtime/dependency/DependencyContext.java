package io.flamingock.oss.core.runtime.dependency;

import io.flamingock.oss.core.runtime.dependency.exception.ForbiddenParameterException;

import java.util.List;
import java.util.Optional;

public interface DependencyContext {

    Optional<Dependency> getDependency(Class<?> type) throws ForbiddenParameterException;

    Optional<Dependency> getDependency(Class<?> type, String name) throws ForbiddenParameterException;

    List<Dependency> getAllDependencies();
}
