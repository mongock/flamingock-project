package io.flamingock.oss.core.process.single;

import io.flamingock.oss.core.process.ExecutableProcess;
import io.flamingock.oss.core.task.executable.ExecutableTask;

import java.util.List;

public class SingleExecutableProcess implements ExecutableProcess {
    protected final List<? extends ExecutableTask> tasks;

    public SingleExecutableProcess(List<? extends ExecutableTask> tasks) {
        this.tasks = tasks;
    }

    public List<? extends ExecutableTask> getTasks() {
        return tasks;
    }
}
