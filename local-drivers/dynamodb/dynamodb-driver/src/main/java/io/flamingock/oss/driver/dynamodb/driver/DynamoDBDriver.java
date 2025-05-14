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

package io.flamingock.oss.driver.dynamodb.driver;

import io.flamingock.commons.utils.RunnerId;
import io.flamingock.core.api.exception.FlamingockException;
import io.flamingock.core.builder.core.CoreConfigurable;
import io.flamingock.core.builder.local.CommunityConfigurable;
import io.flamingock.core.community.LocalEngine;
import io.flamingock.core.community.driver.LocalDriver;
import io.flamingock.core.runtime.dependency.DependencyContext;
import io.flamingock.oss.driver.dynamodb.DynamoDBConfiguration;
import io.flamingock.oss.driver.dynamodb.internal.DynamoDBEngine;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

public class DynamoDBDriver implements LocalDriver {

    private DynamoDbClient client;

    private DynamoDBConfiguration driverConfiguration;

    public DynamoDBDriver() {
    }

    @Override
    public void initialize(DependencyContext dependencyContext) {
        this.client = dependencyContext
                .getDependencyValue(DynamoDbClient.class)
                .orElseThrow(() -> new FlamingockException("DynamoDbClient is needed to be added as dependency"));
        this.driverConfiguration = generateConfig(dependencyContext);
    }

    public DynamoDBConfiguration generateConfig(DependencyContext dependencyContext) {
        DynamoDBConfiguration configuration = dependencyContext
                .getDependencyValue(DynamoDBConfiguration.class)
                .orElse(DynamoDBConfiguration.getDefault());
        dependencyContext.getPropertyAs("dynamodb.autoCreate", boolean.class)
                .ifPresent(configuration::setIndexCreation);
        dependencyContext.getPropertyAs("dynamodb.auditRepositoryName", String.class)
                .ifPresent(d -> {
                    //TODO
                });
        dependencyContext.getPropertyAs("dynamodb.lockRepositoryName", String.class)
                .ifPresent(d -> {
                    //TODO
                });
        dependencyContext.getPropertyAs("dynamodb.readCapacityUnits", int.class)
                .ifPresent(d -> {
                    //TODO
                });
        dependencyContext.getPropertyAs("dynamodb.writeCapacityUnits", int.class)
                .ifPresent(d -> {
                    //TODO
                });
        return configuration;
    }

    @Override
    public LocalEngine initializeAndGetEngine(RunnerId runnerId, CoreConfigurable coreConfiguration, CommunityConfigurable localConfiguration) {
        DynamoDBEngine dynamodbEngine = new DynamoDBEngine(
                client,
                coreConfiguration,
                localConfiguration,
                driverConfiguration);
        dynamodbEngine.initialize(runnerId);
        return dynamodbEngine;
    }

}
