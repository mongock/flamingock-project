package io.flamingock.core.springboot.v2;

import io.flamingock.core.runner.Runner;
import io.flamingock.core.runner.RunnerBuilder;
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
