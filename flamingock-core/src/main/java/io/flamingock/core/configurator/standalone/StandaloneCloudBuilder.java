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

import flamingock.core.api.CloudSystemModule;
import flamingock.core.api.Dependency;
import io.flamingock.commons.utils.JsonObjectMapper;
import io.flamingock.commons.utils.RunnerId;
import io.flamingock.commons.utils.http.Http;
import io.flamingock.core.cloud.CloudConnectionEngine;
import io.flamingock.core.cloud.transaction.CloudTransactioner;
import io.flamingock.core.configurator.cloud.CloudConfiguration;
import io.flamingock.core.configurator.cloud.CloudConfigurator;
import io.flamingock.core.configurator.cloud.CloudConfiguratorDelegate;
import io.flamingock.core.configurator.core.CoreConfiguration;
import io.flamingock.core.configurator.core.CoreConfiguratorDelegate;
import io.flamingock.core.pipeline.Stage;
import io.flamingock.core.runner.PipelineRunnerCreator;
import io.flamingock.core.runner.Runner;
import io.flamingock.core.runtime.dependency.DependencyInjectableContext;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

public class StandaloneCloudBuilder
        extends AbstractStandaloneBuilder<StandaloneCloudBuilder>
        implements CloudConfigurator<StandaloneCloudBuilder> {
    private static final Logger logger = LoggerFactory.getLogger(StandaloneCloudBuilder.class);

    private final CoreConfiguratorDelegate<StandaloneCloudBuilder> coreConfiguratorDelegate;

    private final StandaloneConfiguratorDelegate<StandaloneCloudBuilder> standaloneConfiguratorDelegate;

    private final CloudConfiguratorDelegate<StandaloneCloudBuilder> cloudConfiguratorDelegate;


    StandaloneCloudBuilder(CoreConfiguration coreConfiguration,
                           CloudConfiguration cloudConfiguration,
                           DependencyInjectableContext dependencyInjectableContext) {
        this.coreConfiguratorDelegate = new CoreConfiguratorDelegate<>(coreConfiguration, () -> this);
        this.standaloneConfiguratorDelegate = new StandaloneConfiguratorDelegate<>(dependencyInjectableContext, () -> this);
        this.cloudConfiguratorDelegate = new CloudConfiguratorDelegate<>(cloudConfiguration, () -> this);

    }


    ///////////////////////////////////////////////////////////////////////////////////
    //  BUILD
    ///////////////////////////////////////////////////////////////////////////////////

    @Override
    protected CoreConfiguratorDelegate<StandaloneCloudBuilder> coreConfiguratorDelegate() {
        return coreConfiguratorDelegate;
    }

    @Override
    protected StandaloneConfiguratorDelegate<StandaloneCloudBuilder> standaloneConfiguratorDelegate() {
        return standaloneConfiguratorDelegate;
    }

    @Override
    public Runner build() {
        RunnerId runnerId = RunnerId.generate();
        logger.info("Generated runner id:  {}", runnerId);


        CloudConnectionEngine.Factory engineFactory = CloudConnectionEngine.newFactory(
                runnerId,
                coreConfiguratorDelegate.getCoreConfiguration(),
                cloudConfiguratorDelegate.getCloudConfiguration(),
                getCloudTransactioner().orElse(null),
                Http.builderFactory(HttpClients.createDefault(), JsonObjectMapper.DEFAULT_INSTANCE)
        );

        CloudConnectionEngine engine = engineFactory.initializeAndGet();

        processSystemModules(engine);

        registerTemplates();
        return PipelineRunnerCreator.create(
                runnerId,
                buildPipeline(),
                engine,
                coreConfiguratorDelegate.getCoreConfiguration(),
                buildEventPublisher(),
                getDependencyContext(),
                getCoreConfiguration().isThrowExceptionIfCannotObtainLock(),
                engineFactory.getCloser()
        );
    }

    private void processSystemModules(CloudConnectionEngine engine) {
        cloudConfiguratorDelegate.getSystemModules().forEach(systemModule -> {
            systemModule.initialise(engine.getEnvironmentId(), engine.getServiceId());
            Iterable<Dependency> dependencies = systemModule.getDependencies();
            dependencies.forEach(dependency -> addDependency(dependency.getName(), dependency.getType(), dependency.getInstance()));

            Stage stage = new Stage("");
            stage.setClasses(systemModule.getTaskClasses());
            addStage(stage);//todo wrong. it's appended to the end. We need at start or indicating the oder
        });
    }


    @Override
    public StandaloneCloudBuilder setHost(String host) {
        return cloudConfiguratorDelegate.setHost(host);
    }

    @Override
    public StandaloneCloudBuilder setService(String service) {
        return cloudConfiguratorDelegate.setService(service);
    }

    @Override
    public StandaloneCloudBuilder setEnvironment(String environment) {
        return cloudConfiguratorDelegate.setEnvironment(environment);
    }

    @Override
    public StandaloneCloudBuilder setApiToken(String clientSecret) {
        return cloudConfiguratorDelegate.setApiToken(clientSecret);
    }

    @Override
    public StandaloneCloudBuilder setCloudTransactioner(CloudTransactioner cloudTransactioner) {
        return cloudConfiguratorDelegate.setCloudTransactioner(cloudTransactioner);
    }

    @Override
    public Optional<CloudTransactioner> getCloudTransactioner() {
        return cloudConfiguratorDelegate.getCloudTransactioner();
    }

    @Override
    public StandaloneCloudBuilder addSystemModule(CloudSystemModule systemModule) {
        return cloudConfiguratorDelegate.addSystemModule(systemModule);
    }

    @Override
    public Iterable<CloudSystemModule> getSystemModules() {
        return cloudConfiguratorDelegate.getSystemModules();
    }

}
