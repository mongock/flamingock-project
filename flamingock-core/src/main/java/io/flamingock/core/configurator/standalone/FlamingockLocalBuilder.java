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

package io.flamingock.core.configurator.standalone;

import io.flamingock.commons.utils.RunnerId;
import io.flamingock.core.configurator.core.CoreConfiguration;
import io.flamingock.core.configurator.local.CommunityConfiguration;
import io.flamingock.core.configurator.local.LocalConfigurator;
import io.flamingock.core.configurator.local.LocalSystemModuleManager;
import io.flamingock.core.engine.ConnectionEngine;
import io.flamingock.core.local.LocalEngine;
import io.flamingock.core.local.driver.LocalDriver;
import io.flamingock.core.runtime.dependency.DependencyInjectableContext;

public class FlamingockLocalBuilder
        extends AbstractFlamingockBuilder<FlamingockLocalBuilder>
        implements LocalConfigurator<FlamingockLocalBuilder> {

    private final LocalSystemModuleManager systemModuleManager;

    private final CommunityConfiguration communityConfiguration;

    private LocalDriver<?> connectionDriver;

    private LocalEngine engine;

    protected FlamingockLocalBuilder(CoreConfiguration coreConfiguration,
                                     CommunityConfiguration communityConfiguration,
                                     DependencyInjectableContext dependencyInjectableContext,
                                     LocalSystemModuleManager systemModuleManager) {
        super(coreConfiguration, dependencyInjectableContext, systemModuleManager);
        this.communityConfiguration = communityConfiguration;
        this.systemModuleManager = systemModuleManager;
    }

    @Override
    protected FlamingockLocalBuilder getSelf() {
        return this;
    }

    @Override
    protected ConnectionEngine getConnectionEngine(RunnerId runnerId) {
        //TODO get the driver from serviceLoader
        engine = connectionDriver.initializeAndGetEngine(
                runnerId,
                coreConfiguration,
                communityConfiguration
        );
        return engine;
    }

    @Override
    protected void configureSystemModules() {
        //TODO change this
        engine.getMongockLegacyImporterModule().ifPresent(systemModuleManager::add);
        systemModuleManager.initialize();
    }

    @Override
    @Deprecated
    public FlamingockLocalBuilder setDriver(LocalDriver<?> connectionDriver) {
        this.connectionDriver = connectionDriver;
        return this;
    }

    @Override
    public LocalDriver<?> getDriver() {
        return connectionDriver;
    }

    @Override
    public FlamingockLocalBuilder disableTransaction() {
        communityConfiguration.setTransactionDisabled(true);
        return this;
    }

    @Override
    public boolean isTransactionDisabled() {
        return communityConfiguration.isTransactionDisabled();
    }
}
