/*
 * Copyright 2023 Flamingock ("https://oss.flamingock.io")
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.flamingock.core.cloud;

import io.flamingock.commons.utils.RunnerId;
import io.flamingock.commons.utils.TimeService;
import io.flamingock.commons.utils.http.Http;
import io.flamingock.commons.utils.id.EnvironmentId;
import io.flamingock.commons.utils.id.ServiceId;
import io.flamingock.core.cloud.api.auth.AuthResponse;
import io.flamingock.core.cloud.audit.HtttpAuditWriter;
import io.flamingock.core.cloud.auth.AuthClient;
import io.flamingock.core.cloud.auth.AuthManager;
import io.flamingock.core.cloud.auth.HttpAuthClient;
import io.flamingock.core.cloud.lock.CloudLockService;
import io.flamingock.core.cloud.lock.client.HttpLockServiceClient;
import io.flamingock.core.cloud.lock.client.LockServiceClient;
import io.flamingock.core.cloud.planner.CloudExecutionPlanner;
import io.flamingock.core.cloud.planner.client.ExecutionPlannerClient;
import io.flamingock.core.cloud.planner.client.HttpExecutionPlannerClient;
import io.flamingock.core.cloud.transaction.CloudTransactioner;
import io.flamingock.core.configurator.cloud.CloudConfigurable;
import io.flamingock.core.configurator.core.CoreConfigurable;
import io.flamingock.core.engine.ConnectionEngine;
import io.flamingock.core.engine.audit.AuditWriter;
import io.flamingock.core.engine.execution.ExecutionPlanner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Optional;

public class CloudConnectionEngineInitialized implements ConnectionEngine {
    private static final Logger logger = LoggerFactory.getLogger(CloudConnectionEngineInitialized.class);



    private final CloudTransactioner cloudTransactioner;

    private final AuditWriter auditWriter;

    private final ExecutionPlanner executionPlanner;



    public CloudConnectionEngineInitialized(AuditWriter auditWriter,
                                            ExecutionPlanner executionPlanner,
                                            CloudTransactioner cloudTransactioner) {
        this.auditWriter = auditWriter;
        this.executionPlanner = executionPlanner;
        this.cloudTransactioner = cloudTransactioner;
    }

    public AuditWriter getAuditWriter() {
        return auditWriter;
    }

    @Override
    public ExecutionPlanner getExecutionPlanner() {
        return executionPlanner;
    }

    @Override
    public Optional<CloudTransactioner> getTransactionWrapper() {
        return Optional.ofNullable(cloudTransactioner);
    }


}
