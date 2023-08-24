package io.mongock.core.process.stubs.task;

import io.flamingock.core.runtime.RuntimeManager;
import io.flamingock.core.task.executable.Rollback;

import java.util.List;

public class FailedTestExecutableTask extends TestExecutableTask {
    public FailedTestExecutableTask(String id) {
        super(id, null);
    }

    @Override
    public void execute(RuntimeManager runtimeHelper) {
        super.execute(runtimeHelper);
        throw new RuntimeException("Deliberate execution at task[" + getDescriptor() + "]");
    }


    @Override
    public void addRollback(Rollback rollback) {

    }

    @Override
    public List<? extends Rollback> getRollbackChain() {
        return null;
    }

}
