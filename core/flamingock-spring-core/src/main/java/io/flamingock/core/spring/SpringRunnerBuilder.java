package io.flamingock.core.spring;

import io.flamingock.core.core.runner.Runner;
import io.flamingock.core.core.runner.RunnerBuilder;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.ApplicationRunner;

public interface SpringRunnerBuilder extends RunnerBuilder {
    default ApplicationRunner buildApplicationRunner() {
        Runner runner = build();
        return args -> runner.execute();
    }

    default InitializingBean buildInitializingBeanRunner() {
        Runner runner = build();
        return runner::execute;
    }

}
