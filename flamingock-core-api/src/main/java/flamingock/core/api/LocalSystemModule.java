package flamingock.core.api;


import java.util.Collection;

public abstract class LocalSystemModule extends SystemModule {
    protected LocalSystemModule(Collection<Class<?>> taskClasses) {
        super(taskClasses);
    }

    abstract public void initialise();
}
