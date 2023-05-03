package io.mongock.core.process.stubs.task;

import io.mongock.core.runtime.DefaultRuntimeHelper;
import io.mongock.core.runtime.RuntimeHelper;

public class FailedTestExecutableTask extends TestExecutableTask {
    public FailedTestExecutableTask(String id) {
        super(id,  null);
    }

    @Override
    public void execute(RuntimeHelper runtimeHelper) {
        super.execute(runtimeHelper);
        throw new RuntimeException("Deliberate execution at task[" + getDescriptor() + "]");
    }
}
