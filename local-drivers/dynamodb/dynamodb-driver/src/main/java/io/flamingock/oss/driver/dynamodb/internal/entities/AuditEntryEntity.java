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

import io.flamingock.community.internal.AuditEntryField;
import io.flamingock.core.engine.audit.writer.AuditEntry;
import io.flamingock.oss.driver.dynamodb.internal.util.DynamoDBConstants;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.*;

import java.time.LocalDateTime;
import java.util.Objects;


@DynamoDbBean
public class AuditEntryEntity {

    protected Boolean systemChange;
    private String partitionKey;
    private String sortKey;
    private String taskId;
    private String stageId;
    private String executionId;
    private String author;
    private LocalDateTime createdAt;
    private String state;
    private String className;
    private String methodName;
    private Object metadata;
    private Long executionMillis;
    private String executionHostname;
    private Object errorTrace;
    private String type;

    public AuditEntryEntity(AuditEntry auditEntry) {
        this.partitionKey = partitionKey(auditEntry.getExecutionId(), auditEntry.getTaskId(), auditEntry.getState());
        this.sortKey = sortKey();
        this.taskId = auditEntry.getTaskId();
        this.stageId = auditEntry.getStageId();
        this.executionId = auditEntry.getExecutionId();
        this.author = auditEntry.getAuthor();
        this.createdAt = auditEntry.getCreatedAt();
        this.state = auditEntry.getState().name();
        this.className = auditEntry.getClassName();
        this.methodName = auditEntry.getMethodName();
        this.metadata = auditEntry.getMetadata();
        this.executionMillis = auditEntry.getExecutionMillis();
        this.executionHostname = auditEntry.getExecutionHostname();
        this.errorTrace = auditEntry.getErrorTrace();
        this.type = auditEntry.getType().name();
        this.systemChange = auditEntry.getSystemChange();
    }

    public AuditEntryEntity() {
    }

    public static String partitionKey(String executionId, String taskId, AuditEntry.Status state) {
        return executionId + '#' + taskId + '#' + state.name();
    }

    public static String sortKey() {
        return DynamoDBConstants.AUDIT_LOG_SORT_PREFIX;
    }

    @DynamoDbPartitionKey
    @DynamoDbAttribute(DynamoDBConstants.AUDIT_LOG_PK)
    public String getPartitionKey() {
        return partitionKey;
    }

    public void setPartitionKey(String partitionKey) {
        this.partitionKey = partitionKey;
    }

    @DynamoDbSortKey
    @DynamoDbAttribute(DynamoDBConstants.AUDIT_LOG_SK)
    public String getSortKey() {
        return sortKey;
    }

    public void setSortKey(String sortKey) {
        this.sortKey = sortKey;
    }

    @DynamoDbAttribute(AuditEntryField.KEY_CHANGE_ID)
    @DynamoDbSecondarySortKey(indexNames = {DynamoDBConstants.AUDIT_LOG_LSI_TASK})
    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    @DynamoDbAttribute(DynamoDBConstants.AUDIT_LOG_STAGE_ID)
    public String getStageId() {
        return stageId;
    }

    public void setStageId(String stageId) {
        this.stageId = stageId;
    }

    @DynamoDbAttribute(AuditEntryField.KEY_EXECUTION_ID)
    public String getExecutionId() {
        return executionId;
    }

    public void setExecutionId(String executionId) {
        this.executionId = executionId;
    }

    @DynamoDbAttribute(AuditEntryField.KEY_AUTHOR)
    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    @DynamoDbAttribute(AuditEntryField.KEY_TIMESTAMP)
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @DynamoDbAttribute(AuditEntryField.KEY_STATE)
    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    @DynamoDbAttribute(AuditEntryField.KEY_CHANGELOG_CLASS)
    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    @DynamoDbAttribute(AuditEntryField.KEY_CHANGESET_METHOD)
    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    @DynamoDbAttribute(AuditEntryField.KEY_METADATA)
    public String getMetadata() {
        return metadata.toString();
    }

    public void setMetadata(Object metadata) {
        this.metadata = metadata;
    }

    @DynamoDbAttribute(AuditEntryField.KEY_EXECUTION_MILLIS)
    public Long getExecutionMillis() {
        return executionMillis;
    }

    public void setExecutionMillis(Long executionMillis) {
        this.executionMillis = executionMillis;
    }

    @DynamoDbAttribute(AuditEntryField.KEY_EXECUTION_HOSTNAME)
    public String getExecutionHostname() {
        return executionHostname;
    }

    public void setExecutionHostname(String executionHostname) {
        this.executionHostname = executionHostname;
    }

    @DynamoDbAttribute(AuditEntryField.KEY_ERROR_TRACE)
    public String getErrorTrace() {
        return errorTrace.toString();
    }

    public void setErrorTrace(Object errorTrace) {
        this.errorTrace = errorTrace;
    }

    @DynamoDbAttribute(AuditEntryField.KEY_TYPE)
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @DynamoDbAttribute(AuditEntryField.KEY_SYSTEM_CHANGE)
    public Boolean getSystemChange() {
        return systemChange;
    }

    public void setSystemChange(Boolean systemChange) {
        this.systemChange = systemChange;
    }

    public AuditEntry toAuditEntry() {
        return new AuditEntry(
                executionId,
                stageId,
                taskId,
                author,
                createdAt,
                AuditEntry.Status.valueOf(state),
                AuditEntry.ExecutionType.valueOf(type),
                className,
                methodName,
                executionMillis,
                executionHostname,
                metadata,
                systemChange,
                Objects.toString(errorTrace, "")
        );
    }
}
