package io.flamingock.community.internal.driver;

import io.flamingock.community.internal.DriverConfiguration;
import io.flamingock.community.internal.MongockConfiguration;

public interface ConnectionDriver<DRIVER_CONFIGURATION extends DriverConfiguration> {

    ConnectionEngine getConnectionEngine(MongockConfiguration mongockConfiguration);

    ConnectionDriver<DRIVER_CONFIGURATION> setDriverConfiguration(DRIVER_CONFIGURATION configuration);
}
