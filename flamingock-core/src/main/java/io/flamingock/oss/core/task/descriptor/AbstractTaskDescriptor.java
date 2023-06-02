package io.flamingock.oss.core.task.descriptor;

import java.util.Objects;

public abstract class AbstractTaskDescriptor implements TaskDescriptor{

    private final String id;

    private final String order;

    private final boolean runAlways;

    public AbstractTaskDescriptor(String id,
                                  String order,
                                  boolean runAlways) {
        this.id = id;
        this.order = order;
        this.runAlways = runAlways;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getOrder() {
        return order;
    }

    @Override
    public boolean isRunAlways() {
        return runAlways;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AbstractTaskDescriptor)) return false;
        AbstractTaskDescriptor that = (AbstractTaskDescriptor) o;
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
