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
    public void addRollback(Rollback rollback) {

    }

    @Override
    public List<? extends Rollback> getRollbackChain() {
        return null;
    }
}
