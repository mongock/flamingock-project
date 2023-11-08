package io.flamingock.core.springboot.v3.configurator;

public class SpringbootConfiguration implements SpringbootConfigurable {
    private SpringRunnerType runnerType = SpringRunnerType.ApplicationRunner;

    @Override
    public SpringRunnerType getRunnerType() {
        return runnerType;
    }

    @Override
    public void setRunnerType(SpringRunnerType runnerType) {
        this.runnerType = runnerType;
    }
}
