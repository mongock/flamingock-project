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

package io.flamingock.oss.driver.dynamodb.internal.entities;

import io.flamingock.commons.utils.RunnerId;
import io.flamingock.community.internal.lock.LockEntry;
import io.flamingock.community.internal.lock.LockEntryField;
import io.flamingock.core.engine.lock.LockAcquisition;
import io.flamingock.core.engine.lock.LockStatus;
import io.flamingock.oss.driver.dynamodb.internal.util.DynamoDBConstants;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbAttribute;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSortKey;

import java.time.LocalDateTime;
import java.time.ZoneId;


@DynamoDbBean
public class LockEntryEntity {

    private String partitionKey;
    private String sortKey;
    private String key;
    private LockStatus status;
    private String owner;
    private LocalDateTime expiresAt;

    public LockEntryEntity(LockEntry lock) {
        this.partitionKey = lock.getKey();
        this.sortKey = DynamoDBConstants.LOCK_SORT_PREFIX;
        this.key = lock.getKey();
        this.status = lock.getStatus();
        this.owner = lock.getOwner();
        this.expiresAt = lock.getExpiresAt();
    }

    public LockEntryEntity() {
    }

    @DynamoDbPartitionKey
    @DynamoDbAttribute(DynamoDBConstants.LOCK_PK)
    public String getPartitionKey() {
        return partitionKey;
    }

    public void setPartitionKey(String partitionKey) {
        this.partitionKey = partitionKey;
    }

    @DynamoDbSortKey
    @DynamoDbAttribute(DynamoDBConstants.LOCK_SK)
    public String getSortKey() {
        return sortKey;
    }

    public void setSortKey(String sortKey) {
        this.sortKey = sortKey;
    }

    @DynamoDbAttribute(LockEntryField.KEY_FIELD)
    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    @DynamoDbAttribute(LockEntryField.STATUS_FIELD)
    public LockStatus getStatus() {
        return status;
    }

    public void setStatus(LockStatus status) {
        this.status = status;
    }

    @DynamoDbAttribute(LockEntryField.OWNER_FIELD)
    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    @DynamoDbAttribute(LockEntryField.EXPIRES_AT_FIELD)
    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(LocalDateTime expiresAt) {
        this.expiresAt = expiresAt;
    }

    public LockEntry toLockEntry() {
        return new LockEntry(key, status, owner, expiresAt);
    }

    public LockAcquisition getlockAcquisition() {
        long expiration = this.expiresAt.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
        long now = LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
        long diffMillis = expiration - now;
        return new LockAcquisition(RunnerId.fromString(this.owner), diffMillis);
    }
}
