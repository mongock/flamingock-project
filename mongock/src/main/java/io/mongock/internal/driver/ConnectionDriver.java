package io.mongock.internal.driver;

import io.mongock.internal.MongockConfiguration;

public interface ConnectionDriver<DRIVER_CONFIGURATION extends DriverConfiguration> {

    ConnectionEngine getConnectionEngine(MongockConfiguration mongockConfiguration);

    ConnectionDriver<DRIVER_CONFIGURATION> setDriverConfiguration(DRIVER_CONFIGURATION configuration);
}
