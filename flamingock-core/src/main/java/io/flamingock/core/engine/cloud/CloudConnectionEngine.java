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

package io.flamingock.core.engine.cloud;

import io.flamingock.core.configurator.cloud.CloudConfigurable;
import io.flamingock.core.configurator.core.CoreConfigurable;
import io.flamingock.core.configurator.core.ServiceId;
import io.flamingock.core.engine.ConnectionEngine;
import io.flamingock.core.engine.audit.AuditWriter;
import io.flamingock.core.engine.cloud.audit.HtttpAuditWriter;
import io.flamingock.core.engine.cloud.lock.CloudLockService;
import io.flamingock.core.engine.cloud.lock.client.HttpLockServiceClient;
import io.flamingock.core.engine.cloud.lock.client.LockServiceClient;
import io.flamingock.core.engine.cloud.planner.CloudExecutionPlanner;
import io.flamingock.core.engine.cloud.planner.client.ExecutionPlannerClient;
import io.flamingock.core.engine.cloud.planner.client.HttpExecutionPlannerClient;
import io.flamingock.core.engine.execution.ExecutionPlanner;
import io.flamingock.core.runner.RunnerId;
import io.flamingock.core.transaction.TransactionWrapper;
import io.flamingock.core.util.TimeService;
import io.flamingock.core.util.http.Http;

import java.util.Optional;

public class CloudConnectionEngine implements ConnectionEngine {

    private final CoreConfigurable coreConfiguration;

    private final CloudConfigurable cloudConfiguration;
    private final Http.RequestBuilderFactory requestBuilderFactory;

    private AuditWriter auditWriter;

    private ExecutionPlanner executionPlanner;


    public CloudConnectionEngine(CoreConfigurable coreConfiguration,
                                 CloudConfigurable cloudConfiguration,
                                 Http.RequestBuilderFactory requestBuilderFactory) {
        this.coreConfiguration = coreConfiguration;
        this.cloudConfiguration = cloudConfiguration;
        this.requestBuilderFactory = requestBuilderFactory;
    }

    public AuditWriter getAuditWriter() {
        return auditWriter;
    }


    @Override
    public void initialize(RunnerId runnerId) {
        ServiceId serviceId = ServiceId.fromString(cloudConfiguration.getService());

        auditWriter = new HtttpAuditWriter(
                cloudConfiguration.getHost(),
                serviceId,
                runnerId,
                cloudConfiguration.getApiVersion(),
                requestBuilderFactory
        );

        LockServiceClient lockClient = new HttpLockServiceClient(
                cloudConfiguration.getHost(),
                cloudConfiguration.getApiVersion(),
                requestBuilderFactory
        );

        ExecutionPlannerClient executionPlannerClient = new HttpExecutionPlannerClient(
                cloudConfiguration.getHost(),
                cloudConfiguration.getApiVersion(),
                requestBuilderFactory
        );

        executionPlanner = new CloudExecutionPlanner(
                serviceId,
                runnerId,
                executionPlannerClient,
                coreConfiguration,
                new CloudLockService(lockClient),
                TimeService.getDefault()
        );
        //TODO authenticate

    }

    @Override
    public ExecutionPlanner getExecutionPlanner() {
        return executionPlanner;
    }

    @Override
    public Optional<TransactionWrapper> getTransactionWrapper() {
        return Optional.empty();
    }
}
