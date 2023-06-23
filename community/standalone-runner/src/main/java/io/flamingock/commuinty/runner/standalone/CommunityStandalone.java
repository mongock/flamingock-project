package io.flamingock.commuinty.runner.standalone;

import io.flamingock.community.internal.CommunityConfiguration;
import io.flamingock.core.core.configurator.CoreConfiguration;
import io.flamingock.core.core.runtime.dependency.SimpleDependencyInjectableContext;

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
