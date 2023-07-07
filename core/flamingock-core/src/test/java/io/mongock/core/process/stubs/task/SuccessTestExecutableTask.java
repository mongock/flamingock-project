package io.mongock.core.process.stubs.task;

import io.flamingock.core.core.task.Task;
import org.jetbrains.annotations.NotNull;

public class SuccessTestExecutableTask extends TestExecutableTask {
    public SuccessTestExecutableTask(String id) {
        super(id, null);
    }

    @Override
    public String getOrder() {
        return null;
    }
}
