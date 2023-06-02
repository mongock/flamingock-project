package io.flamingock.oss.internal.driver;

import io.flamingock.oss.internal.MongockConfiguration;

public interface ConnectionDriver<DRIVER_CONFIGURATION extends DriverConfiguration> {

    ConnectionEngine getConnectionEngine(MongockConfiguration mongockConfiguration);

    ConnectionDriver<DRIVER_CONFIGURATION> setDriverConfiguration(DRIVER_CONFIGURATION configuration);
}
