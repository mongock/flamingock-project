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

package io.flamingock.core.cloud;

import io.flamingock.core.api.exception.FlamingockException;
import io.flamingock.core.cloud.changes.CloudChange1;
import io.flamingock.core.cloud.changes.CloudChange2;
import io.flamingock.core.cloud.planner.ExecutionPlanResponse;
import io.flamingock.core.cloud.utils.AuditEntryExpectation;
import io.flamingock.core.cloud.utils.CloudMockBuilderOld;
import io.flamingock.core.cloud.utils.MockFlamingockRunnerServer;
import io.flamingock.core.configurator.standalone.FlamingockStandalone;
import io.flamingock.core.configurator.standalone.StandaloneCloudBuilder;
import io.flamingock.core.engine.audit.writer.AuditEntryStatus;
import io.flamingock.core.pipeline.Stage;
import io.flamingock.core.runner.Runner;
import io.flamingock.core.util.ThreadSleeper;
import io.flamingock.core.util.http.Http;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockedConstruction;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.internal.verification.Times;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;

public class CloudEngineTest {

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

    private MockFlamingockRunnerServer mockFlamingockRunnerServer;
    private StandaloneCloudBuilder standaloneCloudBuilder;

    private static final List<AuditEntryExpectation> auditEntries = new LinkedList<>();

    @BeforeAll
    static void beforeAll() {
        auditEntries.add(new

                AuditEntryExpectation(
                "create-persons-table-from-template",
                AuditEntryStatus.EXECUTED,
                CloudChange1.class.getName(),
                "execution"
        ));
        auditEntries.add(new

                AuditEntryExpectation(
                "create-persons-table-from-template-2",
                AuditEntryStatus.EXECUTED,
                CloudChange2.class.getName(),
                "execution"
        ));
    }

    @BeforeEach
    void beforeEach() {
        mockFlamingockRunnerServer = new MockFlamingockRunnerServer()
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
                .setApiToken(apiToken);

        standaloneCloudBuilder = FlamingockStandalone.cloud()
                .setApiToken(apiToken)
                .setHost("http://localhost:" + runnerServerPort)
                .setService(serviceName)
                .setEnvironment(environmentName)
                .addStage(new Stage("stage-1")
                        .setCodePackages(Collections.singletonList("io.flamingock.core.cloud.changes")));
    }


    @Test
    @DisplayName("Should run successfully happy path")
    void happyPath() {
        //GIVEN
        mockFlamingockRunnerServer
                .addSimpleStageExecutionPlanRequest("execution-1", "stage-1", auditEntries)
                .addExecutionResponseForAll()
                .addContinueExecutionPlanResponse()
                .setJwt(jwt);
        mockFlamingockRunnerServer.start();

        //WHEN
        //THEN
        Runner runner = standaloneCloudBuilder
                .build();
        runner.execute();

        //tear down
        mockFlamingockRunnerServer.stop();
    }

    @Test
    @DisplayName("Should perform the right calls to server when sequence: AWAIT, EXECUTE, CONTINUE")
    void shouldPerformRightCallsWhenAwaitExecuteContinue() {
        //GIVEN
        try (MockedStatic<Http> http = Mockito.mockStatic(Http.class);
             MockedConstruction<ThreadSleeper> lockThreadSleeperConstructors = Mockito.mockConstruction(ThreadSleeper.class)) {
            CloudMockBuilderOld cloudMockBuilder = new CloudMockBuilderOld();
            cloudMockBuilder
                    .addAwaitExecutionPlanResponse(3000L)
                    .addSingleExecutionPlanResponse("stage1", "create-persons-table-from-template", "create-persons-table-from-template-2")
                    .addContinueExecutionPlanResponse()
                    .setHttp(http)
                    .mockServer();

            Runner runner = FlamingockStandalone.cloud()
                    .setApiToken("FAKE_API_TOKEN")
                    .setHost("https://fake-cloud-server.io")
                    .setService("test-service")
                    .setEnvironment("test-environment")
                    .addStage(new Stage("stage-name")
                            .setName("stage1")
                            .setCodePackages(Collections.singletonList("io.flamingock.core.cloud.changes")))
                    .build();

            //WHEN
            runner.execute();

            //THEN
            //check second times it doesn't wait
            ThreadSleeper secondThreadSleeper = lockThreadSleeperConstructors.constructed().get(1);
            verify(secondThreadSleeper, new Times(0)).checkThresholdAndWait(anyLong());

            //2 execution plans: First to execute and second to continue
            verify(cloudMockBuilder.getRequestWithBody(), new Times(3)).execute(ExecutionPlanResponse.class);

            //2 audit writes
            verify(cloudMockBuilder.getRequestWithBody(), new Times(2)).execute();

            //DELETE LOCK
            verify(cloudMockBuilder.getBasicRequest(), new Times(1)).execute();
        }
    }

    @Test
    @DisplayName("Should wait and abort when the lock is blocked and the ThreadSleeper eventually throws an exception")
    void shouldWaitAndEventuallyAbort() {
        ///GIVEN
        try (MockedStatic<Http> http = Mockito.mockStatic(Http.class)) {
            CloudMockBuilderOld cloudMockBuilder = new CloudMockBuilderOld();
            cloudMockBuilder
                    .addAwaitExecutionPlanResponse(5000L)
                    .addAwaitExecutionPlanResponse(5000L)
                    .addAwaitExecutionPlanResponse(5000L)
                    .setHttp(http)
                    .mockServer();

            Runner runner = FlamingockStandalone.cloud()
                    .setApiToken("FAKE_API_TOKEN")
                    .setHost("https://fake-cloud-server.io")
                    .setService("test-service")
                    .setEnvironment("test-environment")
                    .addStage(new Stage("stage-name")
                            .setName("stage1")
                            .setCodePackages(Collections.singletonList("io.flamingock.core.cloud.changes")))
                    .setLockTryFrequencyMillis(175L)
                    .setLockQuitTryingAfterMillis(375L)
                    .build();

            //WHEN
            Assertions.assertThrows(FlamingockException.class, runner::execute);

            //THEN
            //2 execution plans: First to continue
            verify(cloudMockBuilder.getRequestWithBody(), new Times(4)).execute(ExecutionPlanResponse.class);

            //0 audit writes
            verify(cloudMockBuilder.getRequestWithBody(), new Times(0)).execute();

            //Lock release
            verify(cloudMockBuilder.getBasicRequest(), new Times(0)).execute();
        }
    }


    @Test
    @DisplayName("Should wait and retry when lock is initially blocked eventually released")
    void shouldWaitAndRetry() {
        //GIVEN
        try (MockedStatic<Http> http = Mockito.mockStatic(Http.class)) {
            String guid = UUID.randomUUID().toString();
            CloudMockBuilderOld cloudMockBuilder = new CloudMockBuilderOld();
            cloudMockBuilder
                    .addAwaitExecutionPlanResponse(5000L, guid)
                    .addAwaitExecutionPlanResponse(5000L, guid)
                    .addSingleExecutionPlanResponse("stage1", "create-persons-table-from-template", "create-persons-table-from-template-2")
                    .addContinueExecutionPlanResponse()
                    .setHttp(http)
                    .mockServer();

            Runner runner = FlamingockStandalone.cloud()
                    .setApiToken("FAKE_API_TOKEN")
                    .setHost("https://fake-cloud-server.io")
                    .setService("test-service")
                    .setEnvironment("test-environment")
                    .addStage(new Stage("stage-name")
                            .setName("stage1")
                            .setCodePackages(Collections.singletonList("io.flamingock.core.cloud.changes")))
                    .setLockTryFrequencyMillis(3000L)
                    .setLockQuitTryingAfterMillis(9000L)
                    .build();
            //WHEN
            runner.execute();

            //THEN
            //2 execution plans: First to continue
            verify(cloudMockBuilder.getRequestWithBody(), new Times(4)).execute(ExecutionPlanResponse.class);

            //0 audit writes
            verify(cloudMockBuilder.getRequestWithBody(), new Times(2)).execute();

            //Lock release
            verify(cloudMockBuilder.getBasicRequest(), new Times(1)).execute();
        }
    }


    @Test
    @DisplayName("Should continue and not run anything if server returns CONTINUE at first")
    void shouldContinue() {
        try (MockedStatic<Http> http = Mockito.mockStatic(Http.class)) {
            CloudMockBuilderOld cloudMockBuilder = new CloudMockBuilderOld();
            cloudMockBuilder
                    .addContinueExecutionPlanResponse()
                    .setHttp(http)
                    .mockServer();

            Runner runner = FlamingockStandalone.cloud()
                    .setApiToken("FAKE_API_TOKEN")
                    .setHost("https://fake-cloud-server.io")
                    .setService("test-service")
                    .setEnvironment("test-environment")
                    .addStage(new Stage("stage-name")
                            .setName("stage1")
                            .setCodePackages(Collections.singletonList("io.flamingock.core.cloud.changes")))
                    .build();
            runner.execute();

            //2 execution plans: First to continue
            verify(cloudMockBuilder.getRequestWithBody(), new Times(1)).execute(ExecutionPlanResponse.class);
            //0 audit writes
            verify(cloudMockBuilder.getRequestWithBody(), new Times(0)).execute();
            //The lock is not released because it was never took
            verify(cloudMockBuilder.getBasicRequest(), new Times(0)).execute();
        }
    }


}
