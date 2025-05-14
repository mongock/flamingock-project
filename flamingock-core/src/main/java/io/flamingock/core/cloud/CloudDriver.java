package io.flamingock.core.cloud;

import io.flamingock.core.runtime.dependency.DependencyContext;

import java.util.Optional;

public interface CloudDriver {

    void initialize(DependencyContext dependencyContext);

    CloudEngine initializeAndGet();

    //TODO implement get CloudDriver
    static Optional<CloudDriver> getDriver() {
        return Optional.empty();
    }
}
