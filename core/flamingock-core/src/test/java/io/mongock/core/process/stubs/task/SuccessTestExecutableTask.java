package io.mongock.core.process.stubs.task;

import io.flamingock.core.core.task.executable.RollableTask;

import java.util.List;

public class SuccessTestExecutableTask extends TestExecutableTask {
    public SuccessTestExecutableTask(String id) {
        super(id, null);
    }

    public String getOrder() {
        return null;
    }

    @Override
    public void addDependentTask(RollableTask rollbackDependent) {

    }

    @Override
    public List<? extends RollableTask> getDependentTasks() {
        return null;
    }
}
