package io.mongock.core.process.stubs.task;

import io.flamingock.core.core.runtime.RuntimeManager;

public class FailedTestExecutableTask extends TestExecutableTask {
    public FailedTestExecutableTask(String id) {
        super(id, null);
    }

    @Override
    public void execute(RuntimeManager runtimeHelper) {
        super.execute(runtimeHelper);
        throw new RuntimeException("Deliberate execution at task[" + getDescriptor() + "]");
    }
}
