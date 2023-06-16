package io.flamingock.commuinty.runner.standalone;

import io.flamingock.community.internal.CommunityDelegator;
import io.flamingock.community.internal.CommunityProperties;
import io.flamingock.core.core.configurator.CoreDelegator;
import io.flamingock.core.core.configurator.CoreProperties;
import io.flamingock.core.core.configurator.standalone.StandaloneDelegator;
import io.flamingock.core.core.runtime.dependency.SimpleDependencyInjectableContext;

public final class CommunityStandalone {

    private CommunityStandalone() {
    }

    public static CommunityStandaloneBuilder builder() {
        return new CommunityStandaloneBuilder(
                new CoreProperties(),
                new CommunityProperties(),
                new SimpleDependencyInjectableContext());
    }
}
