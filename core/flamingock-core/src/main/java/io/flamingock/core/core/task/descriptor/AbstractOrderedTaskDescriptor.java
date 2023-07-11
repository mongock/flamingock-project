package io.flamingock.core.core.task.descriptor;

public abstract class AbstractOrderedTaskDescriptor extends AbstractTaskDescriptor implements OrderedTaskDescriptor {

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
