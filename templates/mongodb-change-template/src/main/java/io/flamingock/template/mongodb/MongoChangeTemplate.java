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

package io.flamingock.template.mongodb;

import com.mongodb.client.ClientSession;
import com.mongodb.client.MongoDatabase;
import io.flamingock.core.api.template.AbstractChangeTemplate;
import io.flamingock.core.api.template.annotations.ChangeTemplateExecution;
import io.flamingock.core.api.template.annotations.ChangeTemplateRollbackExecution;
import io.flamingock.template.mongodb.model.MongoOperation;
import jakarta.annotation.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MongoChangeTemplate extends AbstractChangeTemplate<MongoChangeTemplateConfig> {

    //avoids static loading at building time to keep GraalVM happy
    protected final Logger logger = LoggerFactory.getLogger("MongoChangeTemplate");

    public MongoChangeTemplate() {
        super(MongoOperation.class);
    }

    @ChangeTemplateExecution
    public void execute(MongoDatabase db, @Nullable ClientSession clientSession) {
        logger.debug("MongoChangeTemplate changes with transaction[{}]", clientSession != null);
        configuration.getChanges().forEach(executionOperation -> executeOp(db, executionOperation, clientSession));
    }

    @ChangeTemplateRollbackExecution
    public void rollback(MongoDatabase db, @Nullable ClientSession clientSession) {
        logger.debug("MongoChangeTemplate rollbacks with transaction[{}]", clientSession != null);
        configuration.getRollbacks().forEach(executionOperation -> executeOp(db, executionOperation, clientSession));
    }

    private void executeOp(MongoDatabase db, MongoOperation op, ClientSession clientSession) {
        op.getOperator(db).apply(clientSession);
    }

}