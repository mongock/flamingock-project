package io.flamingock.commuinty.runner.springboot;

import io.flamingock.community.internal.CommunityProperties;
import io.flamingock.core.core.configurator.CoreProperties;
import io.flamingock.core.spring.configurator.SpringbootProperties;

public final class CommunitySpringboot {

    private CommunitySpringboot() {
    }

    public static CommunitySpringbootBuilder builder() {
        return new CommunitySpringbootBuilder(new CoreProperties(), new CommunityProperties(), new SpringbootProperties());
    }


}
