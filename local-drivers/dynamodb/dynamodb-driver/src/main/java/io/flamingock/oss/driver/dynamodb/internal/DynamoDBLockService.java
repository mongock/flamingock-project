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

package io.flamingock.oss.driver.dynamodb.internal;

import io.flamingock.commons.utils.RunnerId;
import io.flamingock.commons.utils.TimeService;
import io.flamingock.community.internal.lock.LocalLockService;
import io.flamingock.community.internal.lock.LockEntry;
import io.flamingock.core.engine.lock.LockAcquisition;
import io.flamingock.core.engine.lock.LockKey;
import io.flamingock.core.engine.lock.LockServiceException;
import io.flamingock.core.engine.lock.LockStatus;
import io.flamingock.oss.driver.dynamodb.internal.entities.LockEntryEntity;
import io.flamingock.oss.driver.dynamodb.internal.util.DynamoClients;
import io.flamingock.oss.driver.dynamodb.internal.util.DynamoDBConstants;
import io.flamingock.oss.driver.dynamodb.internal.util.DynamoDBUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.PutItemEnhancedRequest;
import software.amazon.awssdk.enhanced.dynamodb.model.UpdateItemEnhancedRequest;
import software.amazon.awssdk.services.dynamodb.model.TransactionCanceledException;

import java.time.LocalDateTime;

import static java.util.Collections.emptyList;

public class DynamoDBLockService implements LocalLockService {

    private static final Logger logger = LoggerFactory.getLogger(DynamoDBLockService.class);

    protected final DynamoClients client;

    private final TimeService timeService;
    private final DynamoDBUtil dynamoDBUtil = new DynamoDBUtil();
    protected DynamoDbTable<LockEntryEntity> table;

    protected DynamoDBLockService(DynamoClients client, TimeService timeService) {
        this.client = client;
        this.timeService = timeService;
    }

    protected void initialize(Boolean indexCreation) {
        if (indexCreation) {
            dynamoDBUtil.createTable(
                    client.getDynamoDbClient(),
                    dynamoDBUtil.getAttributeDefinitions(DynamoDBConstants.LOCK_PK, DynamoDBConstants.LOCK_SK),
                    dynamoDBUtil.getKeySchemas(DynamoDBConstants.LOCK_PK, DynamoDBConstants.LOCK_SK),
                    dynamoDBUtil.getProvisionedThroughput(5L, 5L),
                    DynamoDBConstants.LOCK_TABLE_NAME,
                    emptyList(),
                    emptyList()
            );
        }
        table = client.getEnhancedClient().table(DynamoDBConstants.LOCK_TABLE_NAME, TableSchema.fromBean(LockEntryEntity.class));
    }

    @Override
    public LockAcquisition upsert(LockKey key, RunnerId owner, long leaseMillis) {
        LockEntry newLock = new LockEntry(key.toString(), LockStatus.LOCK_HELD, owner.toString(), timeService.currentDatePlusMillis(leaseMillis));
        LockEntryEntity existingLockEntity = table.getItem(
                Key.builder()
                        .partitionValue(newLock.getKey())
                        .sortValue(DynamoDBConstants.LOCK_SORT_PREFIX)
                        .build()
        );
        if (existingLockEntity != null) {
            LockEntry existingLock = existingLockEntity.toLockEntry();
            if (newLock.getOwner().equals(existingLock.getOwner()) ||
                    LocalDateTime.now().isAfter(existingLock.getExpiresAt())) {
                logger.debug("Lock with key {} already owned by us or is expired, so trying to perform a lock.",
                        existingLock.getKey());
                table.updateItem(
                        UpdateItemEnhancedRequest.builder(LockEntryEntity.class)
                                .item(new LockEntryEntity(newLock))
                                .build()
                );
                logger.debug("Lock with key {} updated", newLock.getKey());

            } else if (LocalDateTime.now().isBefore(existingLock.getExpiresAt())) {
                logger.debug("Already locked by {}, will expire at {}", existingLock.getOwner(),
                        existingLock.getExpiresAt());
                throw new LockServiceException("Get By" + existingLock.getKey(), newLock.toString(),
                        "Still locked by " + existingLock.getOwner() + " until " + existingLock.getExpiresAt());
            }
        } else {
            logger.debug("Lock with key {} does not exist, so trying to perform a lock.", newLock.getKey());
            table.putItem(
                    PutItemEnhancedRequest.builder(LockEntryEntity.class)
                            .item(new LockEntryEntity(newLock))
                            .build()
            );
            logger.debug("Lock with key {} created", newLock.getKey());
        }
        return new LockAcquisition(owner, leaseMillis);
    }

    @Override
    public LockAcquisition extendLock(LockKey key, RunnerId owner, long leaseMillis) throws LockServiceException {
        LockEntry newLock = new LockEntry(key.toString(), LockStatus.LOCK_HELD, owner.toString(), timeService.currentDatePlusMillis(leaseMillis));
        LockEntryEntity existingLockEntity = table.getItem(
                Key.builder()
                        .partitionValue(newLock.getKey())
                        .sortValue(DynamoDBConstants.LOCK_SORT_PREFIX)
                        .build()
        );
        try {
            if (existingLockEntity != null) {
                LockEntry existingLock = existingLockEntity.toLockEntry();
                if (newLock.getOwner().equals(existingLock.getOwner())) {
                    logger.debug("Lock with key {} already owned by us, so trying to perform a lock.",
                            existingLock.getKey());
                    table.updateItem(
                            UpdateItemEnhancedRequest.builder(LockEntryEntity.class)
                                    .item(new LockEntryEntity(newLock))
                                    .build()
                    );
                    logger.debug("Lock with key {} updated", newLock.getKey());
                } else {
                    logger.debug("Already locked by {}, will expire at {}", existingLock.getOwner(),
                            existingLock.getExpiresAt());
                    throw new LockServiceException("Get By " + newLock.getKey(), newLock.toString(),
                            "Lock belongs to " + existingLock.getOwner());
                }
            } else {
                throw new LockServiceException("Get By " + newLock.getKey(), newLock.toString(),
                        "Lock with key " + newLock.getKey() + " not found");
            }
        } catch (TransactionCanceledException ex) {
            throw new LockServiceException("Get By " + newLock.getKey(), newLock.toString(),
                    ex.getMessage());
        }
        return new LockAcquisition(owner, leaseMillis);
    }

    @Override
    public LockAcquisition getLock(LockKey lockKey) {
        LockEntryEntity existingLockEntity = table.getItem(
                Key.builder()
                        .partitionValue(lockKey.toString())
                        .sortValue(DynamoDBConstants.LOCK_SORT_PREFIX)
                        .build()
        );
        if (existingLockEntity != null) {
            return existingLockEntity.getlockAcquisition();
        } else {
            logger.debug("Lock for key {} was not found.", lockKey);
            return null;
        }
    }

    @Override
    public void releaseLock(LockKey lockKey, RunnerId owner) {
        LockEntryEntity existingLockEntity = table.getItem(
                Key.builder()
                        .partitionValue(lockKey.toString())
                        .sortValue(DynamoDBConstants.LOCK_SORT_PREFIX)
                        .build()
        );
        if (existingLockEntity != null) {
            LockEntry existingLock = existingLockEntity.toLockEntry();
            if (owner.equals(RunnerId.fromString(existingLock.getOwner()))) {
                logger.debug("Lock for key {} belongs to us, so removing.", lockKey);
                table.deleteItem(
                        Key.builder()
                                .partitionValue(lockKey.toString())
                                .sortValue(DynamoDBConstants.LOCK_SORT_PREFIX)
                                .build()
                );
            } else {
                logger.debug("Lock for key {} belongs to other owner, can not delete.", existingLock.getKey());
            }
        } else {
            logger.debug("Lock for key {} is not found, nothing to do", lockKey);
        }
    }
}
