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

package io.flamingock.examples.community.dynamodb.changes;

import io.flamingock.core.api.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.DeleteTableRequest;
import software.amazon.awssdk.services.dynamodb.model.DescribeTableRequest;

import static java.util.Collections.emptyList;

@ChangeUnit(id = "creating-table", order = "1")
public class NewTableChangeUnit {

    private static final Logger logger = LoggerFactory.getLogger(NewTableChangeUnit.class);

    private final DynamoDBUtil dynamoDBUtil = new DynamoDBUtil();

    private final String tableName = "test_table";
    private final String pkName = "PartitionKey";
    private final String skName = "SortKey";
    private final Long readCap = 5L;
    private final Long writeCap = 5L;

    @BeforeExecution
    public void beforeExecution(DynamoDbClient client) {
        logger.debug("beforeExecution connected to {}", client.serviceName());
    }

    @RollbackBeforeExecution
    public void rollbackBeforeExecution(DynamoDbClient client) {
        logger.debug("rollbackBeforeExecution connected to {}", client.serviceName());
    }

    @Execution
    public void execution(DynamoDbClient client) {
        dynamoDBUtil.createTable(
                client,
                dynamoDBUtil.getAttributeDefinitions(pkName, skName),
                dynamoDBUtil.getKeySchemas(pkName, skName),
                dynamoDBUtil.getProvisionedThroughput(readCap, writeCap),
                tableName,
                emptyList(),
                emptyList()
        );
        client.describeTable(
                DescribeTableRequest.builder()
                        .tableName(tableName)
                        .build()
        );
    }

    @RollbackExecution
    public void rollbackExecution(DynamoDbClient client) {
        client.deleteTable(
                DeleteTableRequest.builder()
                        .tableName(tableName)
                        .build()
        );
    }

}
