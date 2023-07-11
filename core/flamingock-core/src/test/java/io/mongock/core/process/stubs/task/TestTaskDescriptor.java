package io.mongock.core.process.stubs.task;

import io.flamingock.core.core.task.descriptor.TaskDescriptor;

public class TestTaskDescriptor implements TaskDescriptor {
    private final String id;
    private final boolean rollable;
    private final boolean runAlways;

    public TestTaskDescriptor(String id, boolean rollable, boolean runAlways) {
        this.id = id;
        this.rollable = rollable;
        this.runAlways = runAlways;
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
    public String getClassImplementor() {
        return "testClassImplementor";
    }

    @Override
    public String pretty() {
        return toString();
    }

}
