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

import io.flamingock.template.annotations.TemplateConfigSetter;
import io.flamingock.template.annotations.TemplateConfigValidator;
import io.flamingock.template.annotations.TemplateExecution;
import io.flamingock.template.annotations.TemplateRollbackExecution;

import com.mongodb.client.MongoDatabase;
import io.flamingock.template.mongodb.model.MongoOperation;
import io.flamingock.template.mongodb.model.MongoOperatorFactory;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.util.Map;

public class MongoChangeTemplate {

    private MongoChangeTemplateConfig config;

    @TemplateConfigSetter
    public void setConfiguration(MongoChangeTemplateConfig config) {
        this.config = config;
    }

    @TemplateConfigValidator
    public void validate() {
        config.validate();
    }

    //TODO if transactional we need to add clientSession to the dependency
    @TemplateExecution
    public void execute(MongoDatabase db) {
        config.getChanges().forEach(executionOperation -> executeOp(db, executionOperation));
    }

    @TemplateRollbackExecution
    public void rollback(MongoDatabase db) {
        config.getRollbacks().forEach(executionOperation -> executeOp(db, executionOperation));
    }

    private void executeOp(MongoDatabase db, MongoOperation op) {
        MongoOperatorFactory
                .getOperator(db, op)
                .execute();

//        switch (op.getType()) {
//            case "createCollection":
//                db.createCollection(op.getCollection());
//                break;
//
//            case "createIndex":
//                db.getCollection(op.getCollection())
//                        .createIndex(op.getKeys(), op.getOptions());
//                break;
//
//            case "insert":
//                db.getCollection(op.getCollection())
//                        .insertMany(op.getDocuments());
//                break;
//
//            case "update":
//                db.getCollection(op.getCollection()).updateMany(
//                        op.getFilter(),
//                        new Document((Map<String, Object>) op.getParameters().get("update"))
//                );
//                break;
//        }
    }

    private Bson parseBson(Map<String, Object> map) {
        return new Document(map);
    }
}