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

package io.flamingock.oss.driver.dynamodb.changes.happyPathWithTransaction;

import io.flamingock.core.api.annotations.Change;
import io.flamingock.core.api.annotations.Execution;
import io.flamingock.oss.driver.dynamodb.changes.common.UserEntity;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.TransactWriteItemsEnhancedRequest;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

@Change(id = "insert-user", order = "2")
public class BInsertRow {

    @Execution
    public void execution(DynamoDbClient client, TransactWriteItemsEnhancedRequest.Builder writeRequestBuilder) {
        DynamoDbTable<UserEntity> table = DynamoDbEnhancedClient.builder()
                .dynamoDbClient(client)
                .build()
                .table(UserEntity.tableName, TableSchema.fromBean(UserEntity.class));

        writeRequestBuilder.addPutItem(table, new UserEntity("Pepe", "Pérez"));
    }
}
