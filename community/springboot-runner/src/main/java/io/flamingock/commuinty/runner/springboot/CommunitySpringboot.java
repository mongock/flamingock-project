package io.flamingock.commuinty.runner.springboot;

import io.flamingock.community.internal.CommunityProperties;
import io.flamingock.core.core.configurator.CoreProperties;
import io.flamingock.core.spring.configurator.SpringbootProperties;

public final class CommunitySpringboot {

    private CommunitySpringboot() {
    }

    public static CommunitySpringbootBuilder builder() {
        return builder(new CoreProperties(), new CommunityProperties(), new SpringbootProperties());
    }

    static CommunitySpringbootBuilder builder(CoreProperties coreProperties,
                                              CommunityProperties communityProperties,
                                              SpringbootProperties springbootProperties) {
        return new CommunitySpringbootBuilder(coreProperties, communityProperties, springbootProperties);
    }
}
