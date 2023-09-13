package io.flamingock.core.task.descriptor;

import java.util.Objects;
import java.util.Optional;

public abstract class AbstractTaskDescriptor implements TaskDescriptor {

    private final String id;

    private final String order;

    private final boolean runAlways;
    
    private final boolean transactional;

    public AbstractTaskDescriptor(String id,
                                  String order,
                                  boolean runAlways,
                                  boolean transactional) {
        this.id = id;
        this.order = order;
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
    public Optional<String> getOrder() {
        return Optional.ofNullable(order);
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
