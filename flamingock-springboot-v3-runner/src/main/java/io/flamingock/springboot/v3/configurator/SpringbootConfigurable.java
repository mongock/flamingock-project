package io.flamingock.springboot.v3.configurator;

public interface SpringbootConfigurable {
    SpringRunnerType getRunnerType();

    void setRunnerType(SpringRunnerType runnerType);
}
