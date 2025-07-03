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

package io.flamingock.community.dynamodb.changes;

import io.flamingock.api.annotations.ChangeUnit;
import io.flamingock.api.annotations.Execution;
import io.flamingock.api.annotations.RollbackExecution;
import io.flamingock.community.dynamodb.changes.common.UserEntity;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.DeleteItemEnhancedRequest;
import software.amazon.awssdk.enhanced.dynamodb.model.PutItemEnhancedRequest;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

@ChangeUnit(id = "execution-with-exception", order = "003", transactional = false)
public class _003_insert_jorge_failed_non_transactional_rollback {

    @Execution
    public void execution(DynamoDbClient client) {
        DynamoDbTable<UserEntity> table = DynamoDbEnhancedClient.builder()
                .dynamoDbClient(client)
                .build()
                .table(UserEntity.tableName, TableSchema.fromBean(UserEntity.class));

        table.putItem(
                PutItemEnhancedRequest.builder(UserEntity.class)
                        .item(new UserEntity("Pablo", "López"))
                        .build()
        );
        throw new RuntimeException("test");
    }

    @RollbackExecution
    public void rollbackExecution(DynamoDbClient client) {
        DynamoDbTable<UserEntity> table = DynamoDbEnhancedClient.builder()
                .dynamoDbClient(client)
                .build()
                .table(UserEntity.tableName, TableSchema.fromBean(UserEntity.class));

        table.deleteItem(
                DeleteItemEnhancedRequest.builder()
                        .key(Key.builder().partitionValue("Pablo López").sortValue("Usuario").build())
                        .build()
        );
    }
}
