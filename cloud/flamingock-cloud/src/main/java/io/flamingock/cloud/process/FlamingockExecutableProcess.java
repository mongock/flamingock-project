package io.flamingock.cloud.process;

import io.flamingock.core.core.process.ExecutableProcess;

public class FlamingockExecutableProcess implements ExecutableProcess {
    @Override
    public boolean doesRequireExecution() {
        return true;
    }
}
