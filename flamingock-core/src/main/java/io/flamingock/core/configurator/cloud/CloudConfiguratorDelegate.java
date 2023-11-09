package io.flamingock.core.configurator.cloud;

import java.util.function.Supplier;

public class CloudConfiguratorDelegate<HOLDER> implements CloudConfigurator<HOLDER> {

    private final Supplier<HOLDER> holderSupplier;

    private final CloudConfiguration cloudConfiguration;


    public CloudConfiguratorDelegate(CloudConfiguration cloudConfiguration, Supplier<HOLDER> holderSupplier) {
        this.holderSupplier = holderSupplier;
        this.cloudConfiguration = cloudConfiguration;

    }

    @Override
    public HOLDER setApiKey(String apiKey) {
        cloudConfiguration.setApiKey(apiKey);
        return holderSupplier.get();
    }

    @Override
    public HOLDER setToken(String token) {
        cloudConfiguration.setToken(token);
        return holderSupplier.get();
    }

    @Override
    public String getApiKey() {
        return cloudConfiguration.getApiKey();
    }

    @Override
    public String getToken() {
        return cloudConfiguration.getToken();
    }
}
