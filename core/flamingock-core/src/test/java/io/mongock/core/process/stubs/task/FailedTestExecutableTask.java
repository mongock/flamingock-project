package io.mongock.core.process.stubs.task;

import io.flamingock.core.core.runtime.RuntimeManager;
import io.flamingock.core.core.task.executable.Rollback;

import java.util.List;
import java.util.Optional;

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
    public Optional<Rollback> getRollback() {
        return Optional.empty();
    }

    @Override
    public void addDependentRollbacks(Rollback rollbackDependent) {

    }

    @Override
    public List<? extends Rollback> getDependentTasks() {
        return null;
    }

}
