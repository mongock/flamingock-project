package io.flamingock.core.driver;

import io.flamingock.core.configurator.CommunityConfiguration;
import io.flamingock.core.configurator.local.LocalConfigurable;
import io.flamingock.core.configurator.CoreConfiguration;

public interface ConnectionDriver<DRIVER_CONFIGURATION extends LocalConfigurable> {

    ConnectionEngine getConnectionEngine(CoreConfiguration coreConfiguration, CommunityConfiguration communityConfiguration);

    ConnectionDriver<DRIVER_CONFIGURATION> setDriverConfiguration(DRIVER_CONFIGURATION configuration);
}
