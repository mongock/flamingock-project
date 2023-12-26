/*
 * Copyright 2023 Flamingock ("https://oss.flamingock.io")
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.flamingock.cloud.transaction.sql;

public enum SqlDialect {

    MYSQL("ONGOING_TASKS",
            "CREATE TABLE ONGOING_TASKS (task_id VARCHAR(255) not NULL, operation VARCHAR(10), PRIMARY KEY ( task_id ))",
            "SELECT task_id, operation FROM ONGOING_TASKS",
            "INSERT INTO ONGOING_TASKS (task_id, operation) values(?, ?)",
            "DELETE FROM ONGOING_TASKS WHERE task_id=?"
    );

    private final String tableName;
    private final String createTable;
    private final String query;
    private final String insertTemplate;
    private final String deleteTemplate;

    SqlDialect(String tableName,
               String createTable,
               String query,
               String insertTemplate,
               String deleteTemplate) {
        this.tableName = tableName;
        this.createTable = createTable;
        this.query = query;
        this.insertTemplate = insertTemplate;
        this.deleteTemplate = deleteTemplate;
    }

    public String getTableName() {
        return tableName;
    }

    public String getCreateTable() {
        return createTable;
    }

    public String getQuery() {
        return query;
    }

    public String getInsertTemplate() {
        return insertTemplate;
    }

    public String getDeleteTemplate() {
        return deleteTemplate;
    }
}
