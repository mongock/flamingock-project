package io.flamingock.core.configurator;

import io.flamingock.core.driver.ConnectionDriver;
import io.flamingock.core.configurator.CommunityConfiguration;

public interface LocalConfigurator<HOLDER> {
    HOLDER setDriver(ConnectionDriver<?> connectionDriver);

    ConnectionDriver<?> getDriver();

    CommunityConfiguration getCommunityProperties();
}
