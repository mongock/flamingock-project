package io.flamingock.core.configurator.cloud;

import io.flamingock.core.configurator.CommunityConfiguration;
import io.flamingock.core.driver.ConnectionDriver;

public interface CloudConfigurator<HOLDER> {
    HOLDER setApiKey(String apiKey);

    HOLDER setToken(String token);

    String getApiKey();
    String getToken();

}
