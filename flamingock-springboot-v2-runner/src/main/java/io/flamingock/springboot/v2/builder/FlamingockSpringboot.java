package io.flamingock.springboot.v2.builder;

import io.flamingock.core.configurator.CoreConfiguration;
import io.flamingock.core.configurator.cloud.CloudConfiguration;
import io.flamingock.core.configurator.local.LocalConfigurable;
import io.flamingock.core.configurator.local.LocalConfiguration;
import io.flamingock.springboot.v2.configurator.SpringbootConfiguration;

public final class FlamingockSpringboot {

    private FlamingockSpringboot() {
    }

    public static SpringbootLocalBuilder local() {
        return localBuilder(new CoreConfiguration(), new SpringbootConfiguration(), new LocalConfiguration());
    }

    public static SpringbootCloudBuilder cloud() {
        return cloudBuilder(new CoreConfiguration(), new SpringbootConfiguration(), new CloudConfiguration());
    }

    public static SpringbootLocalBuilder localBuilder(CoreConfiguration coreConfiguration,
                                               SpringbootConfiguration springbootConfiguration,
                                               LocalConfigurable localConfiguration) {
        return new SpringbootLocalBuilder(coreConfiguration, springbootConfiguration, localConfiguration);
    }

    public static SpringbootCloudBuilder cloudBuilder(CoreConfiguration coreConfiguration,
                                               SpringbootConfiguration springbootConfiguration,
                                               CloudConfiguration cloudConfiguration) {
        return new SpringbootCloudBuilder(coreConfiguration, springbootConfiguration, cloudConfiguration);
    }
}
