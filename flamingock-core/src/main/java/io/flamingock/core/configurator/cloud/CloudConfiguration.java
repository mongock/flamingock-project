package io.flamingock.core.configurator.cloud;

public class CloudConfiguration implements CloudConfigurable {

    private String apiKey;

    private String token;

    @Override
    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    @Override
    public void setToken(String token) {
        this.token = token;
    }

    @Override
    public String getApiKey() {
        return apiKey;
    }

    @Override
    public String getToken() {
        return token;
    }
}
