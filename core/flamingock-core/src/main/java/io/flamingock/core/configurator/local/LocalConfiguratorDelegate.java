package io.flamingock.core.configurator.local;

import io.flamingock.core.driver.ConnectionDriver;

import java.util.function.Supplier;

public class LocalConfiguratorDelegate<HOLDER> implements LocalConfigurator<HOLDER> {

    private final LocalConfigurable LocalConfiguration;
    private final Supplier<HOLDER> holderSupplier;
    private ConnectionDriver<?> connectionDriver;

    public LocalConfiguratorDelegate(LocalConfigurable communityConfiguration, Supplier<HOLDER> holderSupplier) {
        this.LocalConfiguration = communityConfiguration;
        this.holderSupplier = holderSupplier;

    }

    @Override
    public HOLDER setDriver(ConnectionDriver<?> connectionDriver) {
        this.connectionDriver = connectionDriver;
        return holderSupplier.get();
    }

    @Override
    public ConnectionDriver<?> getDriver() {
        return connectionDriver;
    }

    @Override
    public LocalConfigurable getLocalProperties() {
        return LocalConfiguration;
    }
}
