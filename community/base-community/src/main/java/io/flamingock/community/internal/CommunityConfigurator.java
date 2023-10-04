package io.flamingock.community.internal;

import io.flamingock.community.internal.driver.ConnectionDriver;

public interface CommunityConfigurator<HOLDER> {
    HOLDER setDriver(ConnectionDriver<?> connectionDriver);

    ConnectionDriver<?> getDriver();

    CommunityConfiguration getCommunityProperties();
}
