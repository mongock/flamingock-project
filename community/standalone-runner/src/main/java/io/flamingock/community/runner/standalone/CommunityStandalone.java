package io.flamingock.community.runner.standalone;

import io.flamingock.core.configurator.CommunityConfiguration;
import io.flamingock.core.configurator.CoreConfiguration;
import io.flamingock.core.runtime.dependency.SimpleDependencyInjectableContext;

public final class CommunityStandalone {

    private CommunityStandalone() {
    }

    public static CommunityStandaloneBuilder builder() {
        return new CommunityStandaloneBuilder(
                new CoreConfiguration(),
                new CommunityConfiguration(),
                new SimpleDependencyInjectableContext());
    }
}
