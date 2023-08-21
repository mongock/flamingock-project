package io.mongock.core.process.stubs.task;

import io.flamingock.core.core.runtime.RuntimeManager;
import io.flamingock.core.core.task.executable.RollableTask;

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
    public void addDependentTask(RollableTask rollbackDependent) {

    }

    @Override
    public List<? extends RollableTask> getDependentTasks() {
        return null;
    }

}
