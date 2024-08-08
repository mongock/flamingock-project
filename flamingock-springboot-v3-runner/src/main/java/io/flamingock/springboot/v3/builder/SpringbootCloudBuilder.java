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

import flamingock.core.api.CloudSystemModule;
import io.flamingock.commons.utils.JsonObjectMapper;
import io.flamingock.commons.utils.http.Http;
import io.flamingock.core.cloud.CloudConnectionEngineCloser;
import io.flamingock.core.cloud.transaction.CloudTransactioner;
import io.flamingock.core.configurator.cloud.CloudConfiguration;
import io.flamingock.core.configurator.cloud.CloudConfigurator;
import io.flamingock.core.configurator.cloud.CloudConfiguratorDelegate;
import io.flamingock.core.configurator.core.CoreConfiguration;
import io.flamingock.core.cloud.CloudConnectionEngine;
import io.flamingock.core.engine.ConnectionEngine;
import io.flamingock.core.runner.PipelineRunnerCreator;
import io.flamingock.core.runner.Runner;
import io.flamingock.commons.utils.RunnerId;
import io.flamingock.springboot.v3.SpringDependencyContext;
import io.flamingock.springboot.v3.SpringRunnerBuilder;
import io.flamingock.springboot.v3.SpringUtil;
import io.flamingock.springboot.v3.configurator.SpringbootConfiguration;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Optional;

public class SpringbootCloudBuilder extends SpringbootBaseBuilder<SpringbootCloudBuilder>
        implements
        CloudConfigurator<SpringbootCloudBuilder>,
        SpringRunnerBuilder {

    private static final Logger logger = LoggerFactory.getLogger(SpringbootCloudBuilder.class);


    private final CloudConfiguratorDelegate<SpringbootCloudBuilder> cloudConfiguratorDelegate;


    SpringbootCloudBuilder(CoreConfiguration coreConfiguration,
                           SpringbootConfiguration springbootConfiguration,
                           CloudConfiguration cloudConfiguration) {
        super(coreConfiguration, springbootConfiguration);
        this.cloudConfiguratorDelegate = new CloudConfiguratorDelegate<>(cloudConfiguration, this::getSelf);
    }

    @Override
    protected SpringbootCloudBuilder getSelf() {
        return this;
    }

    ///////////////////////////////////////////////////////////////////////////////////
    //  BUILD
    ///////////////////////////////////////////////////////////////////////////////////
    @Override
    public Runner build() {
        RunnerId runnerId = RunnerId.generate();
        logger.info("Generated runner id:  {}", runnerId);

        String[] activeProfiles = SpringUtil.getActiveProfiles(getSpringContext());
        logger.info("Creating runner with spring profiles[{}]", Arrays.toString(activeProfiles));

        Http.RequestBuilderFactory requestBuilderFactory = Http.builderFactory(HttpClients.createDefault(), JsonObjectMapper.DEFAULT_INSTANCE);
        CloudTransactioner transactioner = getCloudTransactioner().orElse(null);

        CloudConnectionEngine cloudEngine = ConnectionEngine.initializeAndGetCloud(
                runnerId,
                getCoreConfiguration(),
                cloudConfiguratorDelegate.getCloudConfiguration(),
                transactioner,
                requestBuilderFactory
        );

        CloudConnectionEngineCloser closer = new CloudConnectionEngineCloser(requestBuilderFactory, transactioner);

        return PipelineRunnerCreator.create(
                runnerId,
                buildPipeline(activeProfiles),
                cloudEngine.getAuditWriter(),
                transactioner,
                cloudEngine.getExecutionPlanner(),
                getCoreConfiguration(),
                createEventPublisher(),
                new SpringDependencyContext(getSpringContext()),
                getCoreConfiguration().isThrowExceptionIfCannotObtainLock(),
                closer::close
        );
    }

    ///////////////////////////////////////////////////////////////////////////////////
    //  CLOUD
    ///////////////////////////////////////////////////////////////////////////////////

    @Override
    public SpringbootCloudBuilder setHost(String host) {
        return cloudConfiguratorDelegate.setHost(host);
    }

    @Override
    public SpringbootCloudBuilder setService(String service) {
        return cloudConfiguratorDelegate.setService(service);
    }

    @Override
    public SpringbootCloudBuilder setEnvironment(String environment) {
        return cloudConfiguratorDelegate.setEnvironment(environment);
    }

    @Override
    public SpringbootCloudBuilder setApiToken(String clientSecret) {
        return cloudConfiguratorDelegate.setApiToken(clientSecret);
    }

    @Override
    public SpringbootCloudBuilder setCloudTransactioner(CloudTransactioner cloudTransactioner) {
        return cloudConfiguratorDelegate.setCloudTransactioner(cloudTransactioner);
    }

    @Override
    public Optional<CloudTransactioner> getCloudTransactioner() {
        return cloudConfiguratorDelegate.getCloudTransactioner();
    }

    @Override
    public SpringbootCloudBuilder addSystemModule(CloudSystemModule systemModule) {
        return cloudConfiguratorDelegate.addSystemModule(systemModule);
    }

    @Override
    public Iterable<CloudSystemModule> getSystemModules() {
        return cloudConfiguratorDelegate.getSystemModules();
    }

}
