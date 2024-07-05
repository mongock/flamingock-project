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
import io.flamingock.core.cloud.utils.CloudMockBuilder;
import io.flamingock.core.configurator.standalone.FlamingockStandalone;
import io.flamingock.core.cloud.planner.ExecutionPlanResponse;
import io.flamingock.core.pipeline.Stage;
import io.flamingock.core.runner.Runner;
import io.flamingock.core.util.ThreadSleeper;
import io.flamingock.core.util.http.Http;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.MockedConstruction;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.internal.verification.Times;

import java.util.Collections;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;

public class CloudEngineTest {


    @Test
    @DisplayName("Should run successfully happy path")
    void happyPath() {
        //GIVEN
        try (MockedStatic<Http> http = Mockito.mockStatic(Http.class)) {
            CloudMockBuilder cloudMockBuilder = new CloudMockBuilder();
            String jwt = "fake_jwt";
            cloudMockBuilder
                    .addSingleExecutionPlanResponse("stage1", "create-persons-table-from-template", "create-persons-table-from-template-2")
                    .addContinueExecutionPlanResponse()
                    .setJwtToken(jwt)
                    .setHttp(http)
                    .mockServer();

            Runner runner = FlamingockStandalone.cloud()
                    .setClientId("FAKE_CLIENT_ID")
                    .setApiToken("FAKE_CLIENT_SECRET")
                    .setHost("https://fake-cloud-server.io")
                    .setService("test-service")
                    .addStage(new Stage("stage-name")
                            .setName("stage1")
                            .setCodePackages(Collections.singletonList("io.flamingock.core.cloud.changes")))
                    .build();
            //WHEN
            runner.execute();

            //THEN
            //2 execution plans: First to execute and second to continue
            verify(cloudMockBuilder.getRequestWithBody(), new Times(2)).execute(ExecutionPlanResponse.class);

            //2 audit writes
            verify(cloudMockBuilder.getRequestWithBody(), new Times(2)).execute();

            //DELETE LOCK
            verify(cloudMockBuilder.getBasicRequest(), new Times(1)).execute();

            //AUTH
            ArgumentCaptor<String> jwtCaptorWithBody = ArgumentCaptor.forClass(String.class);
            verify(cloudMockBuilder.getRequestWithBody(), new Times(4)).withBearerToken(jwtCaptorWithBody.capture());

            assertEquals(4, jwtCaptorWithBody.getAllValues().size());
            jwtCaptorWithBody.getAllValues().forEach(actualJwt -> assertEquals(jwt, actualJwt));

            ArgumentCaptor<String> jwtCaptorBasic = ArgumentCaptor.forClass(String.class);
            verify(cloudMockBuilder.getBasicRequest(), new Times(1)).withBearerToken(jwtCaptorBasic.capture());

            assertEquals(jwt, jwtCaptorWithBody.getAllValues().get(0));
        }
    }

    @Test
    @DisplayName("Should perform the right calls to server when sequence: AWAIT, EXECUTE, CONTINUE")
    void shouldPerformRightCallsWhenAwaitExecuteContinue() {
        //GIVEN
        try (MockedStatic<Http> http = Mockito.mockStatic(Http.class);
             MockedConstruction<ThreadSleeper> lockThreadSleeperConstructors = Mockito.mockConstruction(ThreadSleeper.class)) {
            CloudMockBuilder cloudMockBuilder = new CloudMockBuilder();
            cloudMockBuilder
                    .addAwaitExecutionPlanResponse(3000L)
                    .addSingleExecutionPlanResponse("stage1", "create-persons-table-from-template", "create-persons-table-from-template-2")
                    .addContinueExecutionPlanResponse()
                    .setHttp(http)
                    .mockServer();

            Runner runner = FlamingockStandalone.cloud()
                    .setClientId("FAKE_CLIENT_ID")
                    .setApiToken("FAKE_CLIENT_SECRET")
                    .setHost("https://fake-cloud-server.io")
                    .setService("test-service")
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
            CloudMockBuilder cloudMockBuilder = new CloudMockBuilder();
            cloudMockBuilder
                    .addAwaitExecutionPlanResponse(5000L)
                    .addAwaitExecutionPlanResponse(5000L)
                    .addAwaitExecutionPlanResponse(5000L)
                    .setHttp(http)
                    .mockServer();

            Runner runner = FlamingockStandalone.cloud()
                    .setClientId("FAKE_CLIENT_ID")
                    .setApiToken("FAKE_CLIENT_SECRET")
                    .setHost("https://fake-cloud-server.io")
                    .setService("test-service")
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
            CloudMockBuilder cloudMockBuilder = new CloudMockBuilder();
            cloudMockBuilder
                    .addAwaitExecutionPlanResponse(5000L, guid)
                    .addAwaitExecutionPlanResponse(5000L, guid)
                    .addSingleExecutionPlanResponse("stage1", "create-persons-table-from-template", "create-persons-table-from-template-2")
                    .addContinueExecutionPlanResponse()
                    .setHttp(http)
                    .mockServer();

            Runner runner = FlamingockStandalone.cloud()
                    .setClientId("FAKE_CLIENT_ID")
                    .setApiToken("FAKE_CLIENT_SECRET")
                    .setHost("https://fake-cloud-server.io")
                    .setService("test-service")
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
            CloudMockBuilder cloudMockBuilder = new CloudMockBuilder();
            cloudMockBuilder
                    .addContinueExecutionPlanResponse()
                    .setHttp(http)
                    .mockServer();

            Runner runner = FlamingockStandalone.cloud()
                    .setClientId("FAKE_CLIENT_ID")
                    .setApiToken("FAKE_CLIENT_SECRET")
                    .setHost("https://fake-cloud-server.io")
                    .setService("test-service")
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
