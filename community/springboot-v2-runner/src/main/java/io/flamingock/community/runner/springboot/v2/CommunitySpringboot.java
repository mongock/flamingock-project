package io.flamingock.community.runner.springboot.v2;

import io.flamingock.community.internal.CommunityConfiguration;
import io.flamingock.core.configurator.CoreConfiguration;
import io.flamingock.core.springboot.v2.configurator.SpringbootConfiguration;

public final class CommunitySpringboot {

    private CommunitySpringboot() {
    }

    public static CommunitySpringbootBuilder builder() {
        return builder(new CoreConfiguration(), new CommunityConfiguration(), new SpringbootConfiguration());
    }

    static CommunitySpringbootBuilder builder(CoreConfiguration coreConfiguration,
                                              CommunityConfiguration communityConfiguration,
                                              SpringbootConfiguration springbootConfiguration) {
        return new CommunitySpringbootBuilder(coreConfiguration, communityConfiguration, springbootConfiguration);
    }
}
