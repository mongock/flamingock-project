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
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Supplier;

public class SqlCloudTransactioner implements CloudTransactioner {
    private static final Logger logger = LoggerFactory.getLogger(SqlCloudTransactioner.class);

    private final Connection connection;

    public SqlCloudTransactioner(Connection connection) {
        this.connection = connection;
    }

    @Override
    public void initialize() {
        try(Statement statement = connection.createStatement()) {
            statement.executeUpdate(SQL_CREATE_TABLE);
            logger.info("table ONGOING_TASKS created successfully");
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public Set<OngoingStatus> getOngoingStatuses() {
        try(PreparedStatement preparedStatement = connection.prepareStatement(SQL_SELECT)) {
            ResultSet resultSet = preparedStatement.executeQuery();

            Set<OngoingStatus> ongoingStatuses = new HashSet<>();
            while(resultSet.next()) {
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
        try(PreparedStatement preparedStatement = connection.prepareStatement(SQL_DELETE)) {
            preparedStatement.setString(1, taskId);
            int rows = preparedStatement.executeUpdate();
            logger.info("removed ongoing task[{}]: [{}] rows affected", taskId, rows);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void saveOngoingStatus(OngoingStatus status) {
        try(PreparedStatement preparedStatement = connection.prepareStatement(SQL_INSERT)) {
            preparedStatement.setString(1, status.getTaskId());
            preparedStatement.setString(2, status.getOperation().toString());
            int rows = preparedStatement.executeUpdate();
            logger.info("saved ongoing task[{}]: [{}] rows affected", status.getTaskId(), rows);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public <T> T wrapInTransaction(TaskDescriptor taskDescriptor,
                                   DependencyInjectable dependencyInjectable,
                                   Supplier<T> operation) {
        Boolean currentAutoCommit = null;
        try {
            currentAutoCommit = connection.getAutoCommit();
            connection.setAutoCommit(false);
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

        } finally {
            if(currentAutoCommit != null) {
                try {
                    connection.setAutoCommit(currentAutoCommit);
                } catch (SQLException e) {
                    logger.warn(e.getSQLState(), e);
                }
            }
        }
    }

    private final static String SQL_CREATE_TABLE = "CREATE TABLE ONGOING_TASKS (" +
            "task_id VARCHAR(255) not NULL, " +
            " operation VARCHAR(10), " +
            " PRIMARY KEY ( task_id )" +
            ")";

    private final static String SQL_SELECT = "SELECT task_id, operation FROM ONGOING_TASKS";

    private final static String SQL_INSERT = "INSERT INTO ONGOING_TASKS(task_id, operation) values(?, ?)";

    private final static String SQL_DELETE = "DELETE FROM ONGOING_TASKS WHERE task_id=?";
}
