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

package io.flamingock.commons.utils;

import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class DynamoDBUtil {
    private final DynamoDbClient dynamoDbClient;
    private final DynamoDbEnhancedClient enhancedClient;

    public DynamoDBUtil(
            DynamoDbClient dynamoDbClient
    ) {
        this.dynamoDbClient = dynamoDbClient;
        this.enhancedClient = DynamoDbEnhancedClient.builder()
                .dynamoDbClient(dynamoDbClient)
                .build();
    }

    public DynamoDbClient getDynamoDbClient() {
        return dynamoDbClient;
    }

    public DynamoDbEnhancedClient getEnhancedClient() {
        return enhancedClient;
    }

    public List<AttributeDefinition> getAttributeDefinitions(String pkName, String skName, String... vargs) {
        List<AttributeDefinition> result = new ArrayList<>();
        result.add(
                AttributeDefinition.builder()
                        .attributeName(pkName)
                        .attributeType(ScalarAttributeType.S)
                        .build()
        );
        if (skName != null) {
            result.add(
                    AttributeDefinition.builder()
                            .attributeName(skName)
                            .attributeType(ScalarAttributeType.S)
                            .build()
            );
        }
        for (String arg : vargs) {
            result.add(
                    AttributeDefinition.builder()
                            .attributeName(arg)
                            .attributeType(ScalarAttributeType.S)
                            .build()
            );
        }
        return result;
    }

    public List<KeySchemaElement> getKeySchemas(String pkName, String skName) {
        List<KeySchemaElement> result = new ArrayList<>();
        result.add(
                KeySchemaElement.builder()
                        .attributeName(pkName)
                        .keyType(KeyType.HASH)
                        .build()
        );
        if (skName != null) {
            result.add(
                    KeySchemaElement.builder()
                            .attributeName(skName)
                            .keyType(KeyType.RANGE)
                            .build()
            );
        }
        return result;
    }

    public ProvisionedThroughput getProvisionedThroughput(Long readCap, Long writeCap) {
        return ProvisionedThroughput.builder().readCapacityUnits(readCap).writeCapacityUnits(writeCap).build();
    }

    public LocalSecondaryIndex generateLSI(String lsiName, String lsiPK, String lsiSK) {
        return LocalSecondaryIndex.builder()
                .indexName(lsiName)
                .keySchema(
                        Arrays.asList(
                                KeySchemaElement.builder()
                                        .attributeName(lsiPK).
                                        keyType(KeyType.HASH)
                                        .build(),
                                KeySchemaElement.builder()
                                        .attributeName(lsiSK)
                                        .keyType(KeyType.RANGE)
                                        .build()
                        )
                )
                .projection(Projection.builder()
                        .projectionType(ProjectionType.ALL)
                        .build())
                .build();
    }

    public void createTable(
            List<AttributeDefinition> attributeDefinitions,
            List<KeySchemaElement> keySchemas,
            ProvisionedThroughput provisionedVal,
            String tableName,
            List<LocalSecondaryIndex> localSecondaryIndexes,
            List<GlobalSecondaryIndex> globalSecondaryIndexes
    ) {
        try {
            CreateTableRequest.Builder createBuilder = CreateTableRequest.builder()
                    .attributeDefinitions(attributeDefinitions)
                    .keySchema(keySchemas)
                    .provisionedThroughput(provisionedVal)
                    .tableName(tableName);

            if (!localSecondaryIndexes.isEmpty()) {
                createBuilder.localSecondaryIndexes(localSecondaryIndexes);
            }

            if (!globalSecondaryIndexes.isEmpty()) {
                createBuilder.globalSecondaryIndexes(globalSecondaryIndexes);
            }

            DescribeTableRequest tableRequest;
            this.dynamoDbClient.createTable(createBuilder.build());
            tableRequest = DescribeTableRequest.builder()
                    .tableName(tableName)
                    .build();

            this.dynamoDbClient
                    .waiter()
                    .waitUntilTableExists(tableRequest).matched();
        } catch (ResourceInUseException e) {
            // Table already exists, continue
        }
    }
}
