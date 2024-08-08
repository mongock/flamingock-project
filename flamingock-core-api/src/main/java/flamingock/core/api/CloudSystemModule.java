package flamingock.core.api;

import io.flamingock.commons.utils.id.EnvironmentId;
import io.flamingock.commons.utils.id.ServiceId;

import java.util.Collection;

public interface CloudSystemModule extends SystemModule {

    void initialise(EnvironmentId environmentId, ServiceId serviceId);

}
