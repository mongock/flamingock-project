package io.flamingock.core.configurator.local;

import io.flamingock.core.driver.ConnectionDriver;

public interface LocalConfigurator<HOLDER> {
    HOLDER setDriver(ConnectionDriver<?> connectionDriver);

    ConnectionDriver<?> getDriver();

    LocalConfigurable getLocalProperties();
}
