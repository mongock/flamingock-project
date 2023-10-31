package io.flamingock.core.configurator.local;

import io.flamingock.core.configurator.CommunityConfiguration;
import io.flamingock.core.driver.ConnectionDriver;

import java.util.function.Supplier;

public class LocalConfiguratorDelegate<HOLDER> implements LocalConfigurator<HOLDER> {

    private final CommunityConfiguration communityConfiguration;
    private final Supplier<HOLDER> holderSupplier;
    private ConnectionDriver<?> connectionDriver;

    public LocalConfiguratorDelegate(CommunityConfiguration communityConfiguration, Supplier<HOLDER> holderSupplier) {
        this.communityConfiguration = communityConfiguration;
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
    public CommunityConfiguration getCommunityProperties() {
        return communityConfiguration;
    }
}
