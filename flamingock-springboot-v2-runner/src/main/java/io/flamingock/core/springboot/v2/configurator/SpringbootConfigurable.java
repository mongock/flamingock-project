package io.flamingock.core.springboot.v2.configurator;

public interface SpringbootConfigurable {
    SpringRunnerType getRunnerType();

    void setRunnerType(SpringRunnerType runnerType);
}
