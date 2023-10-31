package io.flamingock.core.configurator.cloud;

public interface CloudConfigurable {

    void setApiKey(String apiKey);

    void setToken(String token);

    String getApiKey();
    String getToken();
}
