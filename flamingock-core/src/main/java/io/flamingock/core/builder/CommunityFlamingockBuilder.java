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

package io.flamingock.core.builder;

import io.flamingock.core.builder.core.CoreConfiguration;
import io.flamingock.core.builder.local.CommunityConfiguration;
import io.flamingock.core.builder.local.CommunityConfigurator;
import io.flamingock.core.community.LocalEngine;
import io.flamingock.core.community.driver.LocalDriver;
import io.flamingock.core.runtime.dependency.DependencyInjectableContext;
import io.flamingock.core.system.DefaultSystemModuleManager;

public class CommunityFlamingockBuilder
        extends AbstractFlamingockBuilder<CommunityFlamingockBuilder>
        implements CommunityConfigurator<CommunityFlamingockBuilder> {

    private final DefaultSystemModuleManager systemModuleManager;

    private final CommunityConfiguration communityConfiguration;

    private final LocalDriver driver;

    private LocalEngine engine;

    protected CommunityFlamingockBuilder(CoreConfiguration coreConfiguration,
                                         CommunityConfiguration communityConfiguration,
                                         DependencyInjectableContext dependencyInjectableContext,
                                         DefaultSystemModuleManager systemModuleManager,
                                         LocalDriver driver) {
        super(coreConfiguration, dependencyInjectableContext, systemModuleManager, driver);
        this.communityConfiguration = communityConfiguration;
        this.systemModuleManager = systemModuleManager;
        this.driver = driver;
    }

    @Override
    public CommunityFlamingockBuilder disableTransaction() {
        communityConfiguration.setTransactionDisabled(true);
        return this;
    }

    @Override
    public boolean isTransactionDisabled() {
        return communityConfiguration.isTransactionDisabled();
    }

    @Override
    protected CommunityFlamingockBuilder getSelf() {
        return this;
    }

    @Override
    protected void doInjectDependencies() {
        addDependency(communityConfiguration);
    }

}
