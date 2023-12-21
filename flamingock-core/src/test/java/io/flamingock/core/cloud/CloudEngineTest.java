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

import io.flamingock.core.cloud.utils.CloudMockBuilder;
import io.flamingock.core.configurator.standalone.FlamingockStandalone;
import io.flamingock.core.cloud.planner.ExecutionPlanResponse;
import io.flamingock.core.pipeline.Stage;
import io.flamingock.core.runner.Runner;
import io.flamingock.core.util.ThreadSleeper;
import io.flamingock.core.util.http.Http;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.MockedConstruction;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.internal.verification.Times;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;

public class CloudEngineTest {


    @Test
    @DisplayName("Should run successfully happy path")
    void happyPath() {
        try (MockedStatic<Http> http = Mockito.mockStatic(Http.class)) {
            CloudMockBuilder cloudMockBuilder = new CloudMockBuilder();
            cloudMockBuilder
                    .addSingleExecutionPlanResponse("stage1", "create-persons-table-from-template", "create-persons-table-from-template-2")
                    .addContinueExecutionPlanResponse()
                    .setHttp(http)
                    .mockServer();

            Runner runner = FlamingockStandalone.cloud()
                    .setClientId("FAKE_CLIENT_ID")
                    .setClientSecret("FAKE_CLIENT_SECRET")
                    .setHost("https://fake-cloud-server.io")
                    .setService("test-service")
                    .addStage(new Stage()
                            .setName("stage1")
                            .setCodePackages(Collections.singletonList("io.flamingock.core.cloud.changes")))
                    .build();
            runner.execute();

            //2 execution plans: First to execute and second to continue
            verify(cloudMockBuilder.getRequestWithBody(), new Times(2)).execute(ExecutionPlanResponse.class);
            //2 audit writes
            verify(cloudMockBuilder.getRequestWithBody(), new Times(2)).execute();
            //DELETE LOCK
            verify(cloudMockBuilder.getBasicRequest(), new Times(1)).execute();
        }
    }

    @Test
    @DisplayName("Should wait when the server returns AWAIT")
    void shouldWait() {
        try (MockedStatic<Http> http = Mockito.mockStatic(Http.class);
             MockedConstruction<ThreadSleeper> lockThreadSleeperConstructors = Mockito.mockConstruction(ThreadSleeper.class)) {

            //GIVEN
            CloudMockBuilder cloudMockBuilder = new CloudMockBuilder();
            cloudMockBuilder
                    .addAwaitExecutionPlanResponse(3000L)
                    .addSingleExecutionPlanResponse("stage1", "create-persons-table-from-template", "create-persons-table-from-template-2")
                    .addContinueExecutionPlanResponse()
                    .setHttp(http)
                    .mockServer();
            Runner runner = FlamingockStandalone.cloud()
                    .setClientId("FAKE_CLIENT_ID")
                    .setClientSecret("FAKE_CLIENT_SECRET")
                    .setHost("https://fake-cloud-server.io")
                    .setService("test-service")
                    .addStage(new Stage()
                            .setName("stage1")
                            .setCodePackages(Collections.singletonList("io.flamingock.core.cloud.changes")))
                    .build();

            //WHEN
            runner.execute();

            //THEN
            //executionPlanner did wait
            assertEquals(2, lockThreadSleeperConstructors.constructed().size());
            //check first time it waits for maximum 3 seconds
            ThreadSleeper firstThreadSleeper = lockThreadSleeperConstructors.constructed().get(0);
            ArgumentCaptor<Long> maxMillisWaitingCaptor = ArgumentCaptor.forClass(Long.class);
            verify(firstThreadSleeper, new Times(1)).checkThresholdAndWait(maxMillisWaitingCaptor.capture());
            long maxMillisWaiting = maxMillisWaitingCaptor.getValue();
            assertTrue(maxMillisWaiting <= 3000L);
            assertTrue(maxMillisWaiting > 1000L);
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
    @DisplayName("Should continue and not run anything if server returns CONTINUE")
    void shouldContinue() {
        try (MockedStatic<Http> http = Mockito.mockStatic(Http.class)) {
            CloudMockBuilder cloudMockBuilder = new CloudMockBuilder();
            cloudMockBuilder
                    .addContinueExecutionPlanResponse()
                    .setHttp(http)
                    .mockServer();

            Runner runner = FlamingockStandalone.cloud()
                    .setClientId("FAKE_CLIENT_ID")
                    .setClientSecret("FAKE_CLIENT_SECRET")
                    .setHost("https://fake-cloud-server.io")
                    .setService("test-service")
                    .addStage(new Stage()
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
