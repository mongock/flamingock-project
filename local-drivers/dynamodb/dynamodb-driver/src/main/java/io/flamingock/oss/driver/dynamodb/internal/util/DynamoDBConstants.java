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

package io.flamingock.oss.driver.dynamodb.internal.util;

public final class DynamoDBConstants {
    public static final String AUDIT_LOG_TABLE_NAME = "flamingock_audit_log";
    public static final String AUDIT_LOG_LSI_TASK = "taskLsi";
    public static final String AUDIT_LOG_PK = "partitionKey";
    public static final String AUDIT_LOG_SK = "sortKey";
    public static final String AUDIT_LOG_TASK_ID = "taskId";
    public static final String AUDIT_LOG_STAGE_ID = "stageId";
    public static final String AUDIT_LOG_SORT_PREFIX = "[AUDIT]";

    public static final String LOCK_TABLE_NAME = "locks";
    public static final String LOCK_PK = "partitionKey";
    public static final String LOCK_SK = "sortKey";
    public static final String LOCK_SORT_PREFIX = "[LOCK]";
}
