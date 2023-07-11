package io.flamingock.core.core.process.single;

import io.flamingock.core.core.process.ExecutableProcess;
import io.flamingock.core.core.task.executable.ExecutableTask;
import io.flamingock.core.core.task.executable.OrderedExecutableTask;

import java.util.List;
import java.util.Objects;

public class SingleExecutableProcess implements ExecutableProcess {
    protected final List<? extends ExecutableTask> tasks;

    public SingleExecutableProcess(List<? extends ExecutableTask> tasks) {
        this.tasks = tasks;
    }

    public List<? extends ExecutableTask> getTasks() {
        return tasks;
    }

    @Override
    public boolean doesRequireExecution() {
        return tasks.stream()
                .filter(Objects::nonNull)
                .anyMatch(ExecutableTask::isInitialExecutionRequired);
    }
}
