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

import io.flamingock.common.test.cloud.AuditEntryExpectation;
import io.flamingock.common.test.cloud.MockRunnerServer;
import io.flamingock.core.configurator.standalone.FlamingockStandalone;
import io.flamingock.core.configurator.standalone.StandaloneCloudBuilder;
import io.flamingock.core.pipeline.Stage;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;


//TODO add listener to check final Summary
//TODO verify calls to server
public class CoreBuilderTest {

    @Test
    @DisplayName("Should throw an exception when package of change units empty")
    void happyPath() {

        startServer();

        StandaloneCloudBuilder flamingockBuilder = FlamingockStandalone.cloud()
                .setApiToken("FAKE_API_TOKEN")
                .setHost("http://localhost:8888" )
                .setService("clients-service")
                .setEnvironment("development")
                .addStage(new Stage("stage-1")
                        .setCodePackages(Collections.singletonList("io.flamingock.wrong.package")));
        Exception exception = Assertions.assertThrows(Exception.class, flamingockBuilder::build);

        exception.printStackTrace();


    }

    private void startServer() {
        MockRunnerServer mockRunnerServer = new MockRunnerServer()
                .setServerPort(8888)
                .setOrganisationId(UUID.randomUUID().toString())
                .setOrganisationName("MyOrganisation")
                .setProjectId(UUID.randomUUID().toString())
                .setProjectName("MyOrganisation")
                .setServiceId("clients-service-id")
                .setServiceName("clients-service")
                .setEnvironmentId("development-env-id")
                .setEnvironmentName("development")
                .setCredentialId(UUID.randomUUID().toString())
                .setApiToken("FAKE_API_TOKEN")
                .setJwt("fake_jwt")
                .addExecutionContinueRequestResponse();

        mockRunnerServer.start();
    }


}
