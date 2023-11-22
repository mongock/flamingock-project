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

package io.flamingock.core.engine;

import io.flamingock.core.configurator.cloud.CloudConfiguration;
import io.flamingock.core.engine.audit.AuditWriter;
import io.flamingock.core.engine.execution.ExecutionPlanner;
import io.flamingock.core.transaction.TransactionWrapper;

import java.util.Optional;

public interface CloudConnectionEngine extends ConnectionEngine {

    AuditWriter getAuditWriter();

    void setConfiguration(CloudConfiguration configuration);

    static CloudConnectionEngine getInstance() {
        return new CloudConnectionEngine() {
            @Override
            public AuditWriter getAuditWriter() {
                return null;
            }

            @Override
            public void setConfiguration(CloudConfiguration configuration) {

            }

            @Override
            public void initialize() {

            }

            @Override
            public ExecutionPlanner getExecutionPlanner() {
                return null;
            }

            @Override
            public Optional<TransactionWrapper> getTransactionWrapper() {
                return Optional.empty();
            }
        };
    }

}
