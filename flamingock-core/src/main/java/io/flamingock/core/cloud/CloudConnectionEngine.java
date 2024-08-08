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

import io.flamingock.core.cloud.transaction.CloudTransactioner;
import io.flamingock.core.engine.ConnectionEngine;
import io.flamingock.core.engine.audit.AuditWriter;
import io.flamingock.core.engine.execution.ExecutionPlanner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

public class CloudConnectionEngine implements ConnectionEngine {
    private static final Logger logger = LoggerFactory.getLogger(CloudConnectionEngine.class);



    private final CloudTransactioner cloudTransactioner;

    private final AuditWriter auditWriter;

    private final ExecutionPlanner executionPlanner;



    public CloudConnectionEngine(AuditWriter auditWriter,
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
