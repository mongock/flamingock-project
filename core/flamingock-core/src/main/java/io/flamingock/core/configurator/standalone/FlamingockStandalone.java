package io.flamingock.core.configurator.standalone;

import io.flamingock.core.configurator.local.LocalConfiguration;
import io.flamingock.core.configurator.CoreConfiguration;
import io.flamingock.core.configurator.cloud.CloudConfiguration;
import io.flamingock.core.runtime.dependency.SimpleDependencyInjectableContext;

public final class FlamingockStandalone {

    private FlamingockStandalone() {
    }

    public static StandaloneCloudBuilder cloud() {
        return new StandaloneCloudBuilder(
                new CoreConfiguration(),
                new CloudConfiguration(),
                new SimpleDependencyInjectableContext());
    }

    public static StandaloneLocalBuilder local() {
        return new StandaloneLocalBuilder(
                new CoreConfiguration(),
                new LocalConfiguration(),
                new SimpleDependencyInjectableContext());
    }
}
