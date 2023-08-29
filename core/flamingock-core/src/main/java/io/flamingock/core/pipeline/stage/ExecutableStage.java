package io.flamingock.core.pipeline.stage;

import io.flamingock.core.task.executable.ExecutableTask;

import java.util.List;
import java.util.Objects;

public class ExecutableStage {
    protected final List<? extends ExecutableTask> tasks;
    private final boolean parallel;

    public ExecutableStage(List<? extends ExecutableTask> tasks, boolean parallel) {
        this.tasks = tasks;
        this.parallel = parallel;
    }

    public boolean isParallel() {
        return parallel;
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
