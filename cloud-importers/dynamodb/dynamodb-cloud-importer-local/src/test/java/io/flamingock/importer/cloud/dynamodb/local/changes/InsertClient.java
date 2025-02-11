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

package io.flamingock.importer.cloud.dynamodb.local.changes;

import io.flamingock.core.api.annotations.ChangeUnit;
import io.flamingock.core.api.annotations.Execution;
import io.flamingock.core.api.annotations.NonLockGuarded;
import io.flamingock.importer.cloud.dynamodb.local.common.UserEntity;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.PutItemEnhancedRequest;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

@ChangeUnit( id="insert-row" , order = "1")
public class InsertClient {

    @Execution
    public void execution(@NonLockGuarded DynamoDbClient client) {
        DynamoDbTable<UserEntity> table = DynamoDbEnhancedClient.builder()
                .dynamoDbClient(client)
                .build()
                .table(UserEntity.tableName, TableSchema.fromBean(UserEntity.class));

        table.putItem(
                PutItemEnhancedRequest.builder(UserEntity.class)
                        .item(new UserEntity("Pepe", "Pérez"))
                        .build()
        );
    }
}
