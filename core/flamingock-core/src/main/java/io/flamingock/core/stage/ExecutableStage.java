package io.flamingock.core.stage;

import io.flamingock.core.task.executable.ExecutableTask;

import java.util.List;
import java.util.Objects;

public class ExecutableStage {
    protected final List<? extends ExecutableTask> tasks;

    public ExecutableStage(List<? extends ExecutableTask> tasks) {
        this.tasks = tasks;
    }

    public List<? extends ExecutableTask> getTasks() {
        return tasks;
    }


    public boolean doesRequireExecution() {
        return tasks.stream()
                .filter(Objects::nonNull)
                .anyMatch(ExecutableTask::isInitialExecutionRequired);
    }
}
