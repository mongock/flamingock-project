package io.flamingock.springboot.v2.configurator;

public interface SpringbootConfigurable {
    SpringRunnerType getRunnerType();

    void setRunnerType(SpringRunnerType runnerType);
}
