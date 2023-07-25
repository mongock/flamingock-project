package io.mongock.core.process.stubs.task;

import io.flamingock.core.core.task.descriptor.TaskDescriptor;

public class TestTaskDescriptor implements TaskDescriptor {
    private final String id;
    private final boolean runAlways;
    private final boolean transactional;

    public TestTaskDescriptor(String id, boolean runAlways, boolean transactional) {
        this.id = id;
        this.runAlways = runAlways;
        this.transactional = transactional;
    }

    @Override
    public String getId() {
        return id;
    }


    @Override
    public boolean isRunAlways() {
        return runAlways;
    }

    @Override
    public boolean isTransactional() {
        return transactional;
    }

    @Override
    public String getClassImplementor() {
        return "testClassImplementor";
    }

    @Override
    public String pretty() {
        return toString();
    }

}
