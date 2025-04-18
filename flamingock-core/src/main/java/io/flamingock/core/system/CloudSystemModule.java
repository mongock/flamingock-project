package io.flamingock.core.system;

import io.flamingock.commons.utils.id.EnvironmentId;
import io.flamingock.commons.utils.id.ServiceId;

public interface CloudSystemModule extends SystemModule {

    void initialise(EnvironmentId environmentId, ServiceId serviceId, String jwt, String serverHost);

}
