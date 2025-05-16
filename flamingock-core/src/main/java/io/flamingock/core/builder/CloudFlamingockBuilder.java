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

import io.flamingock.commons.utils.RunnerId;
import io.flamingock.core.cloud.CloudEngine;
import io.flamingock.core.cloud.CloudDriver;
import io.flamingock.core.cloud.transaction.CloudTransactioner;
import io.flamingock.core.builder.cloud.CloudConfiguration;
import io.flamingock.core.builder.cloud.CloudConfigurator;
import io.flamingock.core.builder.core.CoreConfiguration;
import io.flamingock.core.engine.ConnectionEngine;
import io.flamingock.core.runtime.dependency.Dependency;
import io.flamingock.core.runtime.dependency.DependencyInjectableContext;

public class CloudFlamingockBuilder
        extends AbstractFlamingockBuilder<CloudFlamingockBuilder>
        implements CloudConfigurator<CloudFlamingockBuilder> {

    private final DefaultSystemModuleManager systemModuleManager;

    private final CloudConfiguration cloudConfiguration;

    private final CloudDriver driver;

    private CloudTransactioner cloudTransactioner;

    private CloudEngine engine;

    protected CloudFlamingockBuilder(CoreConfiguration coreConfiguration,
                                     CloudConfiguration cloudConfiguration,
                                     DependencyInjectableContext dependencyInjectableContext,
                                     DefaultSystemModuleManager systemModuleManager,
                                     CloudDriver driver) {
        super(coreConfiguration, dependencyInjectableContext, systemModuleManager);
        this.cloudConfiguration = cloudConfiguration;
        this.systemModuleManager = systemModuleManager;
        this.driver = driver;

    }

    @Override
    protected CloudFlamingockBuilder getSelf() {
        return this;
    }


    @Override
    protected void doInjectDependencies() {
        addDependency(cloudConfiguration);

        //TODO get transactioner from ServiceLoader
        // currently injecting it into the context for the driver to pick it up
        if(cloudTransactioner != null) {
            addDependency(new Dependency(CloudTransactioner.class, cloudTransactioner));
        }
    }

    @Override
    protected ConnectionEngine getConnectionEngine() {
        driver.initialize(dependencyContext);
        engine = driver.getEngine();
        return engine;
    }

    @Override
    protected void configureSystemModules() {

    }

    @Override
    public CloudFlamingockBuilder setHost(String host) {
        cloudConfiguration.setHost(host);
        return this;
    }

    @Override
    public CloudFlamingockBuilder setService(String service) {
        cloudConfiguration.setServiceName(service);
        return this;
    }

    @Override
    public CloudFlamingockBuilder setEnvironment(String environment) {
        cloudConfiguration.setEnvironmentName(environment);
        return this;
    }

    @Override
    public CloudFlamingockBuilder setApiToken(String clientSecret) {
        cloudConfiguration.setApiToken(clientSecret);
        return this;
    }

    @Override
    public CloudFlamingockBuilder setCloudTransactioner(CloudTransactioner cloudTransactioner) {
        this.cloudTransactioner = cloudTransactioner;
        return this;
    }


}
