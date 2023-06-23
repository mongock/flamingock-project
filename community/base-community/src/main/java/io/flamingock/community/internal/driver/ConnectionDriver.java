package io.flamingock.community.internal.driver;

import io.flamingock.community.internal.DriverConfigurable;
import io.flamingock.community.internal.CommunityConfiguration;
import io.flamingock.core.core.configurator.CoreConfiguration;

public interface ConnectionDriver<DRIVER_CONFIGURATION extends DriverConfigurable> {

    ConnectionEngine getConnectionEngine(CoreConfiguration coreConfiguration, CommunityConfiguration communityConfiguration);

    ConnectionDriver<DRIVER_CONFIGURATION> setDriverConfiguration(DRIVER_CONFIGURATION configuration);
}
