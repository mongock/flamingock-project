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
import io.flamingock.core.api.annotations.Execution;
import io.flamingock.core.api.annotations.Nullable;
import io.flamingock.core.api.annotations.RollbackExecution;
import io.flamingock.core.api.template.AbstractChangeTemplate;
import io.flamingock.template.mongodb.model.MongoOperation;

public class MongoChangeTemplate extends AbstractChangeTemplate<MongoChangeTemplateConfig> {

    public MongoChangeTemplate() {
        super(MongoChangeTemplateConfig.class, MongoOperation.class);
    }

    @Execution
    public void execute(MongoDatabase db, @Nullable ClientSession clientSession) {
        if(this.isTransactional && clientSession == null) {
            throw new IllegalArgumentException(String.format("Transactional changeUnit[%s] requires transactional ecosystem with ClientSession", changeId));
        }
        executeOp(db, configuration.getExecution(), clientSession);
    }

    @RollbackExecution
    public void rollback(MongoDatabase db, @Nullable ClientSession clientSession) {
        if(this.isTransactional && clientSession == null) {
            throw new IllegalArgumentException(String.format("Transactional changeUnit[%s] requires transactional ecosystem with ClientSession", changeId));
        }
        executeOp(db, configuration.getRollback(), clientSession);
    }

    private void executeOp(MongoDatabase db, MongoOperation op, ClientSession clientSession) {
        op.getOperator(db).apply(clientSession);
    }

}