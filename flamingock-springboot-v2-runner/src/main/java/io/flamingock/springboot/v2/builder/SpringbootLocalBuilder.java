/*
 * Copyright 2023 Flamingock (https://oss.flamingock.io)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.flamingock.springboot.v2.builder;

import io.flamingock.core.configurator.CoreConfiguration;
import io.flamingock.core.configurator.local.LocalConfigurable;
import io.flamingock.core.configurator.local.LocalConfigurator;
import io.flamingock.core.configurator.local.LocalConfiguratorDelegate;
import io.flamingock.core.driver.ConnectionDriver;
import io.flamingock.core.driver.ConnectionEngine;
import io.flamingock.core.runner.Runner;
import io.flamingock.core.runner.RunnerCreator;
import io.flamingock.springboot.v2.SpringDependencyContext;
import io.flamingock.springboot.v2.SpringRunnerBuilder;
import io.flamingock.springboot.v2.SpringUtil;
import io.flamingock.springboot.v2.configurator.SpringbootConfiguration;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;

public class SpringbootLocalBuilder extends SpringbootBaseBuilder<SpringbootLocalBuilder>
        implements
        LocalConfigurator<SpringbootLocalBuilder>,
        SpringRunnerBuilder {

    private static final Logger logger = LoggerFactory.getLogger(SpringbootLocalBuilder.class);


    private final LocalConfiguratorDelegate<SpringbootLocalBuilder> localConfiguratorDelegate;


    SpringbootLocalBuilder(CoreConfiguration coreConfiguration,
                           SpringbootConfiguration springbootConfiguration,
                           LocalConfigurable localConfiguration) {
        super(coreConfiguration, springbootConfiguration);
        this.localConfiguratorDelegate = new LocalConfiguratorDelegate<>(localConfiguration, () -> this);
    }

    ///////////////////////////////////////////////////////////////////////////////////
    //  BUILD
    ///////////////////////////////////////////////////////////////////////////////////
    @Override
    public Runner build() {
        ConnectionEngine connectionEngine = getAndInitilizeConnectionEngine();

        String[] activeProfiles = SpringUtil.getActiveProfiles(getSpringContext());
        logger.info("Creating runner with spring profiles[{}]", Arrays.toString(activeProfiles));

        return RunnerCreator.create(
                buildPipeline(activeProfiles),
                connectionEngine.getAuditor(),
                connectionEngine.getTransactionWrapper().orElse(null),
                connectionEngine.getExecutionPlanner(),
                getCoreProperties(),
                createEventPublisher(),
                new SpringDependencyContext(getSpringContext()),
                getCoreProperties().isThrowExceptionIfCannotObtainLock()
        );
    }

    @Override
    protected SpringbootLocalBuilder getSelf() {
        return this;
    }

    @NotNull
    private ConnectionEngine getAndInitilizeConnectionEngine() {
        ConnectionEngine connectionEngine = localConfiguratorDelegate
                .getDriver()
                .getConnectionEngine(getCoreProperties(), localConfiguratorDelegate.getLocalProperties());
        connectionEngine.initialize();
        return connectionEngine;
    }


    ///////////////////////////////////////////////////////////////////////////////////
    //  LOCAL
    ///////////////////////////////////////////////////////////////////////////////////

    @Override
    public SpringbootLocalBuilder setDriver(ConnectionDriver<?> connectionDriver) {
        return localConfiguratorDelegate.setDriver(connectionDriver);
    }

    @Override
    public ConnectionDriver<?> getDriver() {
        return localConfiguratorDelegate.getDriver();
    }

    @Override
    public LocalConfigurable getLocalProperties() {
        return localConfiguratorDelegate.getLocalProperties();
    }

}
