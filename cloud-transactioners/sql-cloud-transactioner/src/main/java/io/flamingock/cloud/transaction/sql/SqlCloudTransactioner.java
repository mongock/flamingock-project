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

import io.flamingock.core.cloud.transaction.CloudTransactioner;
import io.flamingock.core.cloud.transaction.OngoingStatus;
import io.flamingock.core.engine.audit.domain.AuditItem;
import io.flamingock.core.runtime.dependency.DependencyInjectable;
import io.flamingock.core.task.descriptor.TaskDescriptor;
import io.flamingock.core.task.navigation.step.FailedStep;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Supplier;

public class SqlCloudTransactioner implements CloudTransactioner {
    private static final Logger logger = LoggerFactory.getLogger(SqlCloudTransactioner.class);

    private Connection connection;

    private final String url;

    private final String user;

    private final String password;

    private Dialect dialect = Dialect.MYSQL;

    public SqlCloudTransactioner(String url, String user, String password) {
        this.url = url;
        this.user = user;
        this.password = password;
    }

    @Override
    public void initialize() {
        try {
            connection = DriverManager.getConnection(url, user, password);
            connection.setAutoCommit(false);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        try (Statement statement = connection.createStatement()) {
            DatabaseMetaData meta = connection.getMetaData();
            ResultSet resultSet = meta.getTables(null, null, TABLE_NAME, new String[]{"TABLE"});
            if (!resultSet.next()) {
                statement.executeUpdate(SQL_CREATE_TABLE);
                connection.commit();
                logger.info("table {} created successfully", TABLE_NAME);
            } else {
                logger.debug("Table {} already created", TABLE_NAME);
            }

        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public Set<OngoingStatus> getOngoingStatuses() {
        try (PreparedStatement preparedStatement = connection.prepareStatement(SQL_SELECT)) {
            ResultSet resultSet = preparedStatement.executeQuery();

            Set<OngoingStatus> ongoingStatuses = new HashSet<>();
            while (resultSet.next()) {
                String taskId = resultSet.getString("task_id");
                AuditItem.Operation operation = AuditItem.Operation.valueOf(resultSet.getString("operation"));
                ongoingStatuses.add(new OngoingStatus(taskId, operation));
            }
            return ongoingStatuses;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void cleanOngoingStatus(String taskId) {
        try (PreparedStatement preparedStatement = connection.prepareStatement(SQL_DELETE)) {
            preparedStatement.setString(1, taskId);
            int rows = preparedStatement.executeUpdate();
            logger.info("removed ongoing task[{}]: [{}] rows affected", taskId, rows);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void saveOngoingStatus(OngoingStatus status) {
        try (PreparedStatement preparedStatement = connection.prepareStatement(SQL_INSERT)) {
            preparedStatement.setString(1, status.getTaskId());
            preparedStatement.setString(2, status.getOperation().toString());
            int rows = preparedStatement.executeUpdate();
            connection.commit();
            logger.info("saved ongoing task[{}]: [{}] rows affected", status.getTaskId(), rows);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public <T> T wrapInTransaction(TaskDescriptor taskDescriptor,
                                   DependencyInjectable dependencyInjectable,
                                   Supplier<T> operation) {
        try {
            dependencyInjectable.addDependency(connection);
            T result = operation.get();
            if (result instanceof FailedStep) {
                connection.rollback();
            } else {
                connection.commit();
            }
            return result;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void close() {
        try {
            connection.close();
        } catch (SQLException e) {
            logger.warn("Flamingock SQL Cloud trasactioner connection cannot be closed", e);
        }
    }


    private final static String TABLE_NAME = "ONGOING_TASKS";

    private final static String SQL_CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + " (" +
            "task_id VARCHAR(255) not NULL, " +
            " operation VARCHAR(10), " +
            " PRIMARY KEY ( task_id )" +
            ")";


    private final static String SQL_SELECT = "SELECT task_id, operation FROM " + TABLE_NAME;

    private final static String SQL_INSERT = "INSERT INTO " + TABLE_NAME + "(task_id, operation) values(?, ?)";

    private final static String SQL_DELETE = "DELETE FROM " + TABLE_NAME + " WHERE task_id=?";
}
