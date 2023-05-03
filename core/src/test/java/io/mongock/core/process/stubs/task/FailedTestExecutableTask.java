package io.mongock.core.process.stubs.task;

import io.mongock.core.runtime.DefaultRuntimeHelper;

public class FailedTestExecutableTask extends TestExecutableTask {
    public FailedTestExecutableTask(String id) {
        super(id,  null);
    }

    @Override
    public void execute(DefaultRuntimeHelper runtimeHelper) {
        super.execute(runtimeHelper);
        throw new RuntimeException("Deliberate execution at task[" + getDescriptor() + "]");
    }
}
