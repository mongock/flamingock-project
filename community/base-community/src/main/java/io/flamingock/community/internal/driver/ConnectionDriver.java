package io.flamingock.community.internal.driver;

import io.flamingock.community.internal.DriverConfiguration;
import io.flamingock.community.internal.CommunityConfiguration;

public interface ConnectionDriver<DRIVER_CONFIGURATION extends DriverConfiguration> {

    ConnectionEngine getConnectionEngine(CommunityConfiguration communityConfiguration);

    ConnectionDriver<DRIVER_CONFIGURATION> setDriverConfiguration(DRIVER_CONFIGURATION configuration);
}
