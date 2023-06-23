package io.flamingock.core.spring.configurator;

public interface SpringbootConfigurable {
    SpringRunnerType getRunnerType();

    void setRunnerType(SpringRunnerType runnerType);
}
