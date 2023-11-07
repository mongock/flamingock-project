package io.flamingock.community.runner.springboot.v3;

import io.flamingock.core.configurator.local.LocalConfiguration;
import io.flamingock.core.configurator.CoreConfiguration;
import io.flamingock.core.springboot.v3.configurator.SpringbootConfiguration;

public final class CommunitySpringboot {

    private CommunitySpringboot() {
    }

    public static CommunitySpringbootBuilder builder() {
        return builder(new CoreConfiguration(), new LocalConfiguration(), new SpringbootConfiguration());
    }

    static CommunitySpringbootBuilder builder(CoreConfiguration coreConfiguration,
                                              LocalConfiguration communityConfiguration,
                                              SpringbootConfiguration springbootConfiguration) {
        return new CommunitySpringbootBuilder(coreConfiguration, communityConfiguration, springbootConfiguration);
    }
}
