package io.flamingock.core.configurator.cloud;

public interface CloudConfigurator<HOLDER> {
    HOLDER setApiKey(String apiKey);

    HOLDER setToken(String token);

    String getApiKey();
    String getToken();

}
