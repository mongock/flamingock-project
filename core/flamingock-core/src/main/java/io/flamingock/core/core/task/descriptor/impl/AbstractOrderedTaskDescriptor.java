package io.flamingock.core.core.task.descriptor.impl;

public abstract class AbstractOrderedTaskDescriptor extends AbstractTaskDescriptor {

    private final String order;

    public AbstractOrderedTaskDescriptor(String id,
                                         String order,
                                         boolean runAlways) {
        super(id, runAlways);
        this.order = order;
    }


    @Override
    public String getOrder() {
        return order;
    }

}
