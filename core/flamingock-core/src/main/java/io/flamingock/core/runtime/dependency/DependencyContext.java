package io.flamingock.core.runtime.dependency;

import io.flamingock.core.runtime.dependency.exception.ForbiddenParameterException;

import java.util.Optional;

public interface DependencyContext {

    Optional<Dependency> getDependency(Class<?> type) throws ForbiddenParameterException;

    Optional<Dependency> getDependency(String name) throws ForbiddenParameterException;
}