package flamingock.core.api;


import java.util.Collection;

public abstract class SystemModule {

    private final Collection<Class<?>> taskClasses;

    protected SystemModule(Collection<Class<?>> taskClasses) {
        this.taskClasses = taskClasses;
    }

    public Collection<Class<?>> getTaskClasses() {
        return taskClasses;
    }

    abstract public Iterable<Dependency> getDependencies();
}
