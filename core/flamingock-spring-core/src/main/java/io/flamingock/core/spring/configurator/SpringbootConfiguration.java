package io.flamingock.core.spring.configurator;

public class SpringbootConfiguration {
    private SpringRunnerType runnerType = SpringRunnerType.ApplicationRunner;

    public SpringRunnerType getRunnerType() {
        return runnerType;
    }

    public void setRunnerType(SpringRunnerType runnerType) {
        this.runnerType = runnerType;
    }
}
