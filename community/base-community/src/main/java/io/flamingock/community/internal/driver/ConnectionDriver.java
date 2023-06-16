package io.flamingock.community.internal.driver;

import io.flamingock.community.internal.DriverConfiguration;
import io.flamingock.community.internal.CommunityProperties;
import io.flamingock.core.core.configurator.CoreProperties;

public interface ConnectionDriver<DRIVER_CONFIGURATION extends DriverConfiguration> {

    ConnectionEngine getConnectionEngine(CoreProperties coreProperties, CommunityProperties communityProperties);

    ConnectionDriver<DRIVER_CONFIGURATION> setDriverConfiguration(DRIVER_CONFIGURATION configuration);
}
