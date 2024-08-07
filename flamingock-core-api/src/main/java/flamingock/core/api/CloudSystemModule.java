package flamingock.core.api;

import io.flamingock.commons.utils.id.EnvironmentId;
import io.flamingock.commons.utils.id.ServiceId;

import java.util.Collection;

public abstract class CloudSystemModule extends SystemModule {

    protected CloudSystemModule(Collection<Class<?>> taskClasses) {
        super(taskClasses);
    }

    abstract public void initialise(EnvironmentId environmentId, ServiceId serviceId);

}
