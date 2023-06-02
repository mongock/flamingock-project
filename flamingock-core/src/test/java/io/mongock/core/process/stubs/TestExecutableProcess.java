package io.mongock.core.process.stubs;

import io.flamingock.oss.core.process.single.SingleExecutableProcess;
import io.mongock.core.process.stubs.task.TestExecutableTask;

import java.util.Arrays;
import java.util.List;

public class TestExecutableProcess extends SingleExecutableProcess {

    private final List<TestExecutableTask> tasks;

    public TestExecutableProcess(List<TestExecutableTask> tasks) {
        super(tasks);
        this.tasks = tasks;
    }
    public TestExecutableProcess(TestExecutableTask... tasks) {
        this(Arrays.asList(tasks));
    }

}
