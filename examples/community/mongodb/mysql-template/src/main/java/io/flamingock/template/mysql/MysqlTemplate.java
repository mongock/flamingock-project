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

package io.flamingock.template.mysql;

import io.flamingock.template.annotations.TemplateConfigSetter;
import io.flamingock.template.annotations.TemplateConfigValidator;
import io.flamingock.template.annotations.TemplateExecution;
import io.flamingock.template.annotations.TemplateRollbackExecution;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class MysqlTemplate {

    private MysqlTemplateConfiguration configuration;


    @TemplateConfigSetter
    public void setConfiguration(MysqlTemplateConfiguration configuration) {
        this.configuration = configuration;
    }

    @TemplateConfigValidator
    public boolean validateConfiguration() {
        return configuration.getExecutionSql() != null;
    }

    @TemplateExecution
    public void execution(Connection connection) {
        execute(connection, configuration.getExecutionSql());
    }

    @TemplateRollbackExecution
    public void rollback(Connection connection) {
        execute(connection, configuration.getRollbackSql());
    }

    private static void execute(Connection connection, String sql) {
        try {
            connection.createStatement().executeUpdate(sql);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }



}
