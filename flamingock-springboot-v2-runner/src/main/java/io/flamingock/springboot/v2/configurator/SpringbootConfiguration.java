package io.flamingock.springboot.v2.configurator;

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
