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

package io.flamingock.template.sql;

import io.flamingock.api.annotations.Execution;
import io.flamingock.api.annotations.RollbackExecution;
import io.flamingock.api.template.AbstractChangeTemplate;

import java.sql.Connection;
import java.sql.SQLException;

public class SqlTemplate extends AbstractChangeTemplate<Void, String, String> {

    public SqlTemplate() {
        super();
    }

    @Execution
    public void execution(Connection connection) {
        execute(connection, execution);
    }

    @RollbackExecution
    public void rollback(Connection connection) {
        execute(connection, rollback);
    }

    private static void execute(Connection connection, String sql) {
        try {
            connection.createStatement().executeUpdate(sql);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
