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

package io.flamingock.cloud.transaction.dynamodb;

import com.amazonaws.services.dynamodbv2.local.main.ServerRunner;
import com.amazonaws.services.dynamodbv2.local.server.DynamoDBProxyServer;
import io.flamingock.cloud.transaction.dynamodb.changes.common.UserEntity;
import io.flamingock.cloud.transaction.dynamodb.changes.happypath.HappyCreateTableClientsChange;
import io.flamingock.cloud.transaction.dynamodb.changes.happypath.HappyInsertClientsChange;
import io.flamingock.cloud.transaction.dynamodb.changes.unhappypath.UnhappyCreateTableClientsChange;
import io.flamingock.cloud.transaction.dynamodb.changes.unhappypath.UnhappyInsertionClientsChange;
import io.flamingock.common.test.cloud.AuditEntryExpectation;
import io.flamingock.common.test.cloud.MockRunnerServer;
import io.flamingock.core.cloud.api.transaction.OngoingStatus;
import io.flamingock.core.configurator.standalone.FlamingockStandalone;
import io.flamingock.core.configurator.standalone.StandaloneCloudBuilder;
import io.flamingock.core.pipeline.Stage;
import io.flamingock.core.runner.PipelineExecutionException;
import io.flamingock.core.runner.Runner;
import org.junit.jupiter.api.*;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.ScanEnhancedRequest;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

import java.net.URI;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import static io.flamingock.core.cloud.api.audit.AuditEntryRequest.Status.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class DynamoDBCloudTransactionerTest {

    private static final Logger logger = LoggerFactory.getLogger(DynamoDBCloudTransactionerTest.class);

    private static DynamoDBProxyServer dynamoDBLocal;
    private static DynamoDbClient client;
    private static DynamoDBTestHelper dynamoDBTestHelper;

    private final String apiToken = "FAKE_API_TOKEN";
    private final String organisationId = UUID.randomUUID().toString();
    private final String organisationName = "MyOrganisation";

    private final String projectId = UUID.randomUUID().toString();
    private final String projectName = "MyOrganisation";

    private final String serviceName = "clients-service";
    private final String environmentName = "development";
    private final String serviceId = "clients-service-id";
    private final String environmentId = "development-env-id";
    private final String credentialId = UUID.randomUUID().toString();
    private final int runnerServerPort = 8888;
    private final String jwt = "fake_jwt";

    private MockRunnerServer mockRunnerServer;
    private StandaloneCloudBuilder flamingockBuilder;

    @BeforeEach
    void beforeEach() throws Exception {
        logger.info("Starting DynamoDB Local...");
//        dynamoDBLocal = ServerRunner.createServerFromCommandLineArgs(
//                new String[]{
//                        "-inMemory",
//                        "-port",
//                        "8000"
//                }
//        );
//        dynamoDBLocal.start();

        client = DynamoDbClient.builder()
                .region(Region.EU_WEST_1)
                .endpointOverride(new URI("http://localhost:8000"))
                .credentialsProvider(
                        StaticCredentialsProvider.create(
                                AwsBasicCredentials.create("dummye", "dummye")
                        )
                )
                .build();

        dynamoDBTestHelper = new DynamoDBTestHelper(client);

        logger.info("Starting Mock Server...");
        mockRunnerServer = new MockRunnerServer()
                .setServerPort(runnerServerPort)
                .setOrganisationId(organisationId)
                .setOrganisationName(organisationName)
                .setProjectId(projectId)
                .setProjectName(projectName)
                .setServiceId(serviceId)
                .setServiceName(serviceName)
                .setEnvironmentId(environmentId)
                .setEnvironmentName(environmentName)
                .setCredentialId(credentialId)
                .setApiToken(apiToken)
                .setJwt(jwt);

        flamingockBuilder = FlamingockStandalone.cloud()
                .setApiToken(apiToken)
                .setHost("http://localhost:" + runnerServerPort)
                .setService(serviceName)
                .setEnvironment(environmentName);
    }

    @AfterEach
    void afterEach() throws Exception {
        //tear down
        logger.info("Stopping Mock Server...");
        mockRunnerServer.stop();

        logger.info("Stopping DynamoDB Local...");
        dynamoDBLocal.stop();
    }

    @Test
    @DisplayName("Should follow the transactioner lifecycle")
    void happyPath() {
        List<AuditEntryExpectation> auditEntryExpectations = new LinkedList<>();
        auditEntryExpectations.add(new
                AuditEntryExpectation(
                "create-table-clients",
                EXECUTED,
                HappyCreateTableClientsChange.class.getName(),
                "execution"
        ));
        auditEntryExpectations.add(new
                AuditEntryExpectation(
                "insert-clients",
                EXECUTED,
                HappyInsertClientsChange.class.getName(),
                "execution"
        ));

        //GIVEN
        try (
                DynamoDBCloudTransactioner transactioner = new DynamoDBCloudTransactioner()
        ) {
            String executionId = "execution-1";
            String stageName = "stage-1";
            mockRunnerServer
                    .addSimpleStageExecutionPlan(executionId, stageName, auditEntryExpectations)
                    .addExecutionWithAllTasksRequestResponse(executionId)
                    .addExecutionContinueRequestResponse()
                    .start();

            DynamoDBCloudTransactioner dynamoDBCloudTransactioner = Mockito.spy(transactioner.setDynamoDbClient(client));

            //WHEN
            flamingockBuilder
                    .setCloudTransactioner(dynamoDBCloudTransactioner)
                    .addStage(new Stage(stageName)
                            .setCodePackages(Collections.singletonList("io.flamingock.cloud.transaction.dynamodb.changes.happypath")))
                    .addDependency(client)
                    .build()
                    .execute();

            // check clients changes
            dynamoDBTestHelper.checkCount(
                    DynamoDbEnhancedClient.builder()
                            .dynamoDbClient(client)
                            .build()
                            .table(UserEntity.tableName, TableSchema.fromBean(UserEntity.class)),
                    0);
            // check ongoing status
            dynamoDBTestHelper.checkAtLeastOneOngoingTask();
        }
    }

    @Test
    @DisplayName("Should rollback the ongoing deletion when a task fails")
    void failedTasks() {
        List<AuditEntryExpectation> auditEntryExpectations = new LinkedList<>();
        auditEntryExpectations.add(new
                AuditEntryExpectation(
                "unhappy-create-table-clients",
                EXECUTED,
                UnhappyCreateTableClientsChange.class.getName(),
                "execution"
        ));

        auditEntryExpectations.add(new
                AuditEntryExpectation(
                "unhappy-insert-clients",
                EXECUTION_FAILED,
                UnhappyInsertionClientsChange.class.getName(),
                "execution"
        ));

        auditEntryExpectations.add(new
                AuditEntryExpectation(
                "unhappy-insert-clients",
                ROLLED_BACK,
                UnhappyInsertionClientsChange.class.getName(),
                "native_db_engine"
        ));

        //GIVEN
        try (
                DynamoDBCloudTransactioner transactioner = new DynamoDBCloudTransactioner()
        ) {
            String executionId = "execution-1";
            String stageName = "stage-1";
            mockRunnerServer
                    .addSimpleStageExecutionPlan(executionId, stageName, auditEntryExpectations)
                    .addExecutionWithAllTasksRequestResponse(executionId)
                    .addExecutionContinueRequestResponse()
                    .start();

            DynamoDBCloudTransactioner dynamoDBCloudTransactioner = Mockito.spy(transactioner.setDynamoDbClient(client));

            //WHEN
            Runner runner = flamingockBuilder
                    .setCloudTransactioner(dynamoDBCloudTransactioner)
                    .addStage(new Stage(stageName)
                            .setCodePackages(Collections.singletonList("io.flamingock.cloud.transaction.dynamodb.changes.unhappypath")))
                    .addDependency(client)
                    .build();
            PipelineExecutionException ex = Assertions.assertThrows(PipelineExecutionException.class, runner::run);

            // check clients changes
            dynamoDBTestHelper.checkCount(
                    DynamoDbEnhancedClient.builder()
                            .dynamoDbClient(client)
                            .build()
                            .table(UserEntity.tableName, TableSchema.fromBean(UserEntity.class)),
                    0);
            // check ongoing status
            dynamoDBTestHelper.checkAtLeastOneOngoingTask();
        }
    }


    //TODO verify the server is called with the right parameters. among other, it sends the ongoing status
    @Test
    @DisplayName("Should send ongoing task in execution when is present in local database")
    void shouldSendOngoingTaskInExecutionPlan() {
        List<AuditEntryExpectation> auditEntryExpectations = new LinkedList<>();
        auditEntryExpectations.add(new
                AuditEntryExpectation(
                "unhappy-create-table-clients",
                EXECUTED,
                UnhappyCreateTableClientsChange.class.getName(),
                "execution"
        ));

        auditEntryExpectations.add(new
                AuditEntryExpectation(
                "unhappy-insert-clients",
                EXECUTION_FAILED,
                UnhappyInsertionClientsChange.class.getName(),
                "execution"
        ));

        auditEntryExpectations.add(new
                AuditEntryExpectation(
                "unhappy-insert-clients",
                ROLLED_BACK,
                UnhappyInsertionClientsChange.class.getName(),
                "native_db_engine"
        ));
        String executionId = "execution-1";
        String stageName = "stage-1";

        //GIVEN
        try (
                DynamoDBCloudTransactioner transactioner = new DynamoDBCloudTransactioner()
        ) {
            dynamoDBTestHelper.insertOngoingExecution("failed-insert-clients");
            List<OngoingStatus> ongoingStatuses = Collections.singletonList(new OngoingStatus("failed-insert-clients", OngoingStatus.Operation.EXECUTION));
            mockRunnerServer
                    .addSimpleStageExecutionPlan(executionId, stageName, auditEntryExpectations, ongoingStatuses)
                    .addExecutionWithAllTasksRequestResponse(executionId)
                    .addExecutionContinueRequestResponse()
                    .start();

            DynamoDBCloudTransactioner dynamoDBCloudTransactioner = Mockito.spy(transactioner.setDynamoDbClient(client));

            //WHEN
            Runner runner = flamingockBuilder
                    .setCloudTransactioner(dynamoDBCloudTransactioner)
                    .addStage(new Stage(stageName)
                            .setCodePackages(Collections.singletonList("io.flamingock.cloud.transaction.dynamodb.changes.unhappypath")))
                    .addDependency(client)
                    .build();

            //then
            PipelineExecutionException ex = Assertions.assertThrows(PipelineExecutionException.class, runner::run);
        }
    }
}
