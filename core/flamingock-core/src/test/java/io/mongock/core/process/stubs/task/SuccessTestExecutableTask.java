package io.mongock.core.process.stubs.task;

import io.flamingock.core.core.task.executable.Rollback;

import java.util.List;
import java.util.Optional;

public class SuccessTestExecutableTask extends TestExecutableTask {
    public SuccessTestExecutableTask(String id) {
        super(id, null);
    }

    public String getOrder() {
        return null;
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
