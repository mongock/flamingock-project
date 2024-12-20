package io.flamingock.core.engine.local.driver;

import io.flamingock.commons.utils.RunnerId;
import io.flamingock.core.configurator.core.CoreConfigurable;
import io.flamingock.core.configurator.local.LocalConfigurable;
import io.flamingock.core.engine.local.LocalConnectionEngine;

public interface LocalConnectionEngineFactory {


    default LocalConnectionEngine initializeValidateAndGetEngine(RunnerId runnerId,
                                                 CoreConfigurable coreConfiguration,
                                                 LocalConfigurable localConfiguration) {
        LocalConnectionEngine engine = initializeAndGetEngine(runnerId, coreConfiguration, localConfiguration);
        engine.validate(coreConfiguration);
        return engine;
    }

    LocalConnectionEngine initializeAndGetEngine(RunnerId runnerId,
                                                 CoreConfigurable coreConfiguration,
                                                 LocalConfigurable localConfiguration);
}
