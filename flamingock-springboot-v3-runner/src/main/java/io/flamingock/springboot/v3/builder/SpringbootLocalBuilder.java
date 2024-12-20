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

package io.flamingock.springboot.v3.builder;

import io.flamingock.core.api.LocalSystemModule;
import io.flamingock.commons.utils.RunnerId;
import io.flamingock.core.api.exception.FlamingockException;
import io.flamingock.core.configurator.core.CoreConfigurable;
import io.flamingock.core.configurator.core.CoreConfiguration;
import io.flamingock.core.configurator.local.LocalConfigurable;
import io.flamingock.core.configurator.local.LocalConfigurator;
import io.flamingock.core.configurator.local.LocalConfiguratorDelegate;
import io.flamingock.core.configurator.local.LocalSystemModuleManager;
import io.flamingock.core.engine.local.LocalConnectionEngine;
import io.flamingock.core.engine.local.driver.ConnectionDriver;
import io.flamingock.core.pipeline.Pipeline;
import io.flamingock.core.runner.PipelineRunnerCreator;
import io.flamingock.core.runner.Runner;
import io.flamingock.core.transaction.TransactionWrapper;
import io.flamingock.springboot.v3.SpringDependencyContext;
import io.flamingock.springboot.v3.SpringRunnerBuilder;
import io.flamingock.springboot.v3.SpringUtil;
import io.flamingock.springboot.v3.configurator.SpringbootConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;

public class SpringbootLocalBuilder extends AbstractSpringbootBuilder<SpringbootLocalBuilder, LocalSystemModule, LocalSystemModuleManager>
        implements
        LocalConfigurator<SpringbootLocalBuilder>,
        SpringRunnerBuilder {

    private static final Logger logger = LoggerFactory.getLogger(SpringbootLocalBuilder.class);


    private final LocalConfiguratorDelegate<SpringbootLocalBuilder> localConfiguratorDelegate;


    SpringbootLocalBuilder(CoreConfiguration coreConfiguration,
                           SpringbootConfiguration springbootConfiguration,
                           LocalConfigurable localConfiguration,
                           LocalSystemModuleManager systemModuleManager) {
        super(coreConfiguration, springbootConfiguration, systemModuleManager);
        this.localConfiguratorDelegate = new LocalConfiguratorDelegate<>(localConfiguration, () -> this);
    }

    ///////////////////////////////////////////////////////////////////////////////////
    //  BUILD
    ///////////////////////////////////////////////////////////////////////////////////

    @Override
    public Runner build() {
        RunnerId runnerId = RunnerId.generate();
        logger.info("Generated runner id:  {}", runnerId);

        CoreConfigurable coreConfiguration = getCoreConfiguration();
        LocalConnectionEngine engine = localConfiguratorDelegate.getDriver().initializeAndGetEngine(
                runnerId,
                coreConfiguration,
                localConfiguratorDelegate.getLocalConfiguration()
        );

        checkTransactionalConsistency(engine.getTransactionWrapper().orElse(null));

        //adds Mongock legacy importer, if the user has required it
        engine.getMongockLegacyImporterModule().ifPresent(coreConfiguratorDelegate::addSystemModule);

        getSystemModuleManager().initialize();

        getSystemModuleManager()
                .getDependencies()
                .forEach(this::addDependency);

        String[] activeProfiles = SpringUtil.getActiveProfiles(getSpringContext());
        logger.info("Creating runner with spring profiles[{}]", Arrays.toString(activeProfiles));
        Pipeline pipeline = buildPipeline(activeProfiles,
                getSystemModuleManager().getSortedSystemStagesBefore(),
                coreConfiguration.getStages(),
                getSystemModuleManager().getSortedSystemStagesAfter());

        return PipelineRunnerCreator.create(
                runnerId,
                pipeline,
                getFlamingockMetadata(),
                engine,
                coreConfiguration,
                createEventPublisher(),
                new SpringDependencyContext(getSpringContext()),
                coreConfiguration.isThrowExceptionIfCannotObtainLock()
        );

    }

    private void checkTransactionalConsistency(TransactionWrapper transactionWrapper) {
        Boolean transactionEnabled = coreConfiguratorDelegate.getTransactionEnabled();
        if(transactionWrapper == null && transactionEnabled != null && transactionEnabled) {
            throw new FlamingockException("[transactionEnabled = true] and driver not provided");
        }
    }

    @Override
    protected SpringbootLocalBuilder getSelf() {
        return this;
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
    public LocalConfigurable getLocalConfiguration() {
        return localConfiguratorDelegate.getLocalConfiguration();
    }
}
