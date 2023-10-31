package io.flamingock.community.internal;

import io.flamingock.core.driver.ConnectionDriver;
import io.flamingock.core.configurator.CommunityConfiguration;

public interface CommunityConfigurator<HOLDER> {
    HOLDER setDriver(ConnectionDriver<?> connectionDriver);

    ConnectionDriver<?> getDriver();

    CommunityConfiguration getCommunityProperties();
}
