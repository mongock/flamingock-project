package io.flamingock.core.driver;

import io.flamingock.core.configurator.local.LocalConfiguration;
import io.flamingock.core.configurator.CoreConfiguration;

public interface ConnectionDriver<DRIVER_CONFIGURATION extends DriverConfigurable> {

    ConnectionEngine getConnectionEngine(CoreConfiguration coreConfiguration, LocalConfiguration communityConfiguration);

    ConnectionDriver<DRIVER_CONFIGURATION> setDriverConfiguration(DRIVER_CONFIGURATION configuration);
}