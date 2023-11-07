package io.flamingock.core.configurator.standalone;

import io.flamingock.core.configurator.local.LocalConfiguration;
import io.flamingock.core.configurator.CoreConfiguration;
import io.flamingock.core.configurator.cloud.CloudConfiguration;
import io.flamingock.core.runtime.dependency.SimpleDependencyInjectableContext;

public final class FlamingockStandalone {

    private FlamingockStandalone() {
    }

    public static CloudStandaloneBuilder cloud() {
        return new CloudStandaloneBuilder(
                new CoreConfiguration(),
                new CloudConfiguration(),
                new SimpleDependencyInjectableContext());
    }

    public static LocalStandaloneBuilder local() {
        return new LocalStandaloneBuilder(
                new CoreConfiguration(),
                new LocalConfiguration(),
                new SimpleDependencyInjectableContext());
    }
}
