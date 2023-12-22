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

import io.flamingock.cloud.transaction.sql.utils.CloudMockBuilder;
import io.flamingock.cloud.transaction.sql.utils.TestCloudTransactioner;
import io.flamingock.core.cloud.planner.ExecutionPlanResponse;
import io.flamingock.core.cloud.transaction.OngoingStatus;
import io.flamingock.core.configurator.standalone.FlamingockStandalone;
import io.flamingock.core.engine.audit.domain.AuditItem;
import io.flamingock.core.pipeline.Stage;
import io.flamingock.core.runner.Runner;
import io.flamingock.core.util.http.Http;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.internal.verification.Times;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.utility.DockerImageName;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static org.mockito.Mockito.verify;

public class SqlCloudTransactionerTest {

    private static final MySQLContainer<?> mysql = new MySQLContainer<>(DockerImageName.parse("mysql:5.7.34"));


    @BeforeAll
    static void beforeAll() {
        mysql.start();
    }


    @Test
    @DisplayName("Should follow the transactioner lifecycle")
    void happyPath() throws SQLException {

        String dbUrl = mysql.getJdbcUrl();
        String user = mysql.getUsername();
        String pass = mysql.getPassword();

        try (
                MockedStatic<Http> http = Mockito.mockStatic(Http.class);
                Connection connection = DriverManager.getConnection(dbUrl, user, pass)
        ) {
            CloudMockBuilder cloudMockBuilder = new CloudMockBuilder();
            cloudMockBuilder
                    .addSingleExecutionPlanResponse("stage1", "create-table-clients", "insert-clients")
                    .addContinueExecutionPlanResponse()
                    .setHttp(http)
                    .mockServer();

            SqlCloudTransactioner sqlCloudTransactioner = Mockito.spy(new SqlCloudTransactioner(connection));

            FlamingockStandalone.cloud()
                    .setClientId("FAKE_CLIENT_ID")
                    .setClientSecret("FAKE_CLIENT_SECRET")
                    .setHost("https://fake-cloud-server.io")
                    .setService("test-service")
                    .setCloudTransactioner(sqlCloudTransactioner)
                    .addStage(new Stage()
                            .setName("stage1")
                            .setCodePackages(Collections.singletonList("io.flamingock.cloud.transaction.sql.changes")))
                    .build()
                    .execute();

            //2 execution plans: First to execute and second to continue
            verify(cloudMockBuilder.getRequestWithBody(), new Times(2)).execute(ExecutionPlanResponse.class);
            //2 audit writes
            verify(cloudMockBuilder.getRequestWithBody(), new Times(2)).execute();
            //DELETE LOCK
            verify(cloudMockBuilder.getBasicRequest(), new Times(1)).execute();

            // check clients changes
            try(PreparedStatement preparedStatement = connection.prepareStatement("SELECT id, name FROM CLIENTS")) {
                ResultSet resultSet = preparedStatement.executeQuery();
                int counter = 0;
                while(resultSet.next()) {
                    Assertions.assertEquals(1, resultSet.getInt(1));
                    Assertions.assertEquals("Robert Mccoy",  resultSet.getString(2));
                    counter++;
                }
                Assertions.assertEquals(1, counter);
            }

            // check ongoing status
            try(PreparedStatement preparedStatement = connection.prepareStatement("SELECT count(task_id) FROM ONGOING_TASKS")) {
                ResultSet resultSet = preparedStatement.executeQuery();
                while(resultSet.next()) {
                    Assertions.assertEquals(0, resultSet.getInt(1));

                }
            }


        }
    }




}
