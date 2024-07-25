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

package io.flamingock.core.cloud.utils;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.ScenarioMappingBuilder;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.github.tomakehurst.wiremock.stubbing.Scenario;
import io.flamingock.core.cloud.auth.AuthRequest;
import io.flamingock.core.cloud.auth.AuthResponse;
import io.flamingock.core.cloud.planner.ExecutionPlanRequest;
import io.flamingock.core.cloud.planner.ExecutionPlanResponse;
import io.flamingock.core.cloud.planner.StageRequest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.delete;
import static com.github.tomakehurst.wiremock.client.WireMock.equalToJson;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static io.flamingock.core.cloud.utils.JsonMapper.toJson;

public final class MockFlamingockRunnerServer {

    public static final String DEFAULT_LOCK_ACQUISITION = UUID.randomUUID().toString();
    private final List<ExecutionPlanResponse> executionPlanResponses = new ArrayList<>();
    private ExecutionExpectation executionExpectation = null;

    private static final long DEFAULT_ACQUIRED_FOR_MILLIS = 60000L;
    private WireMockServer wireMockServer;

    private int serverPort = 8888;

    private String organisationId = "default-organisation-id";
    private String organisationName = "default-organisation-name";

    private String projectId = "default-project-id";
    private String projectName = "default-project-name";

    private String serviceId = "default-service-id";
    private String serviceName = "default-service-name";

    private String environmentId = "default-environment-name";
    private String environmentName = "default-environment-name";

    private String runnerId = "default-runner-name";

    private String jwt = "default-jwt";

    private String apiToken = "default-api-token";

    private String credentialId = "default-credential-id";

    public MockFlamingockRunnerServer setServerPort(int serverPort) {
        this.serverPort = serverPort;
        return this;
    }

    public MockFlamingockRunnerServer setApiToken(String apiToken) {
        this.apiToken = apiToken;
        return this;
    }

    public MockFlamingockRunnerServer setOrganisationId(String organisationId) {
        this.organisationId = organisationId;
        return this;
    }

    public MockFlamingockRunnerServer setOrganisationName(String organisationName) {
        this.organisationName = organisationName;
        return this;
    }

    public MockFlamingockRunnerServer setProjectId(String projectId) {
        this.projectId = projectId;
        return this;
    }

    public MockFlamingockRunnerServer setProjectName(String projectName) {
        this.projectName = projectName;
        return this;
    }

    public MockFlamingockRunnerServer setCredentialId(String credentialId) {
        this.credentialId = credentialId;
        return this;
    }

    public MockFlamingockRunnerServer setServiceId(String serviceId) {
        this.serviceId = serviceId;
        return this;
    }

    public MockFlamingockRunnerServer setServiceName(String serviceName) {
        this.serviceName = serviceName;
        return this;
    }

    public MockFlamingockRunnerServer setEnvironmentId(String environmentId) {
        this.environmentId = environmentId;
        return this;
    }

    public MockFlamingockRunnerServer setEnvironmentName(String environmentName) {
        this.environmentName = environmentName;
        return this;
    }

    public MockFlamingockRunnerServer setRunnerId(String runnerId) {
        this.runnerId = runnerId;
        return this;
    }

    public MockFlamingockRunnerServer addContinueExecutionPlanResponse() {
        ExecutionPlanResponse executionPlanResponse = new ExecutionPlanResponse();
        executionPlanResponse.setAction(ExecutionPlanResponse.Action.CONTINUE);
        executionPlanResponses.add(executionPlanResponse);
        return this;
    }

    public MockFlamingockRunnerServer addAwaitExecutionPlanResponse(long acquiredForMillis) {
        return addAwaitExecutionPlanResponse(UUID.randomUUID().toString(), acquiredForMillis, UUID.randomUUID().toString());
    }

    public MockFlamingockRunnerServer addAwaitExecutionPlanResponse(String executionId, long acquiredForMillis, String guid) {
        ExecutionPlanResponse executionPlanResponse = new ExecutionPlanResponse();
        executionPlanResponse.setExecutionId(executionId);
        executionPlanResponse.setAction(ExecutionPlanResponse.Action.AWAIT);
        ExecutionPlanResponse.Lock lock = new ExecutionPlanResponse.Lock();
        lock.setAcquisitionId(guid);
        lock.setKey(serviceName);
        lock.setOwner(runnerId);
        lock.setAcquiredForMillis(acquiredForMillis);
        executionPlanResponse.setLock(lock);
        executionPlanResponses.add(executionPlanResponse);
        return this;
    }

    public MockFlamingockRunnerServer addSimpleStageExecutionPlanRequest(String executionId,
                                                                         String stageName,
                                                                         List<AuditEntryExpectation> auditEntries) {

        List<StageRequest.Task> tasks = auditEntries.stream()
                .map(auditEntryExpectation -> StageRequest.Task.task(auditEntryExpectation.getTaskId()))
                .collect(Collectors.toList());


        StageRequest stageRequest = new StageRequest(stageName, 0, tasks);

        executionExpectation = new ExecutionExpectation(executionId, stageRequest, auditEntries, 60000, 0);
        return this;
    }

    public MockFlamingockRunnerServer addExecutionResponseForAll() {
        executionPlanResponses.add(new FullExecutionPlanResponse());
        return this;
    }

    public MockFlamingockRunnerServer setJwt(String jwt) {
        this.jwt = jwt;
        return this;
    }


    public void start() {
        wireMockServer = new WireMockServer(WireMockConfiguration.wireMockConfig().port(serverPort));
        wireMockServer.start();

        mockAuthEndpoint();
        mockExecutionEndpoint();
        mockAuditWriteEndpoint();
        mockReleaseLockEndpoint();
    }

    public void stop() {
        if (wireMockServer != null && wireMockServer.isRunning()) {
            wireMockServer.stop();
        }
    }

    private void mockAuthEndpoint() {

        AuthRequest authRequest = new AuthRequest(apiToken, serviceName, environmentName);

        AuthResponse tokenResponse = new AuthResponse();
        tokenResponse.setJwt(jwt);
        tokenResponse.setCredentialId(credentialId);
        tokenResponse.setOrganisationId(organisationId);
        tokenResponse.setOrganisationName(organisationName);
        tokenResponse.setProjectId(projectId);
        tokenResponse.setProjectName(projectName);
        tokenResponse.setServiceId(serviceId);
        tokenResponse.setServiceName(serviceName);
        tokenResponse.setEnvironmentId(environmentId);
        tokenResponse.setEnvironmentName(environmentName);

        wireMockServer.stubFor(post(urlPathEqualTo("/api/v1/auth/exchange-token"))
                .withRequestBody(equalToJson(toJson(authRequest)))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(toJson(tokenResponse))
                ));


    }


    private void mockExecutionEndpoint() {
        String executionUrl = "/api/v1/environment/{environmentId}/service/{serviceId}/execution"//?elapsedMillis={elapsedMillis}"
                .replace("{environmentId}", environmentId)
                .replace("{serviceId}", serviceId)
//                .replace("{elapsedMillis}", String.valueOf(executionExpectation.getElapsedMillis()))
                ;

        List<StageRequest> stages = Collections.singletonList(executionExpectation.getStageRequest());

        if (executionPlanResponses.size() == 1) {

            ExecutionPlanResponse response = getExecutionPlanResponse(0);
            ExecutionPlanRequest request = new ExecutionPlanRequest(response.getLock().getAcquiredForMillis(), stages);
            wireMockServer.stubFor(post(urlPathEqualTo(executionUrl))
                    .withRequestBody(equalToJson(toJson(request)))
                    .willReturn(aResponse()
                            .withStatus(201)
                            .withHeader("Content-Type", "application/json")
                            .withBody(toJson(response))
                    ));

        } else {
            String scenarioName = "Execution-plan-request";
            for (int i = 0; i < executionPlanResponses.size(); i++) {

                String scenarioState = i == 0
                        ? Scenario.STARTED
                        : "execution-state-" + i;

                ExecutionPlanResponse response = getExecutionPlanResponse(i);
                long acquiredForMillis = response.getLock() != null
                        ? response.getLock().getAcquiredForMillis()
                        : DEFAULT_ACQUIRED_FOR_MILLIS;
                ExecutionPlanRequest request = new ExecutionPlanRequest(acquiredForMillis, stages);

                ScenarioMappingBuilder scenarioMappingBuilder = post(urlPathEqualTo(executionUrl))
                        .inScenario(scenarioName)
                        .whenScenarioStateIs(scenarioState);
                if (i < executionPlanResponses.size() - 1) {
                    scenarioMappingBuilder.willSetStateTo("execution-state-" + (i + 1));
                }
                String json = toJson(request);
                wireMockServer.stubFor(scenarioMappingBuilder
                        .withRequestBody(equalToJson(json))
                        .willReturn(aResponse()
                                .withStatus(201)
                                .withHeader("Content-Type", "application/json")
                                .withBody(toJson(response))
                        ));
            }


        }

    }


    private void mockAuditWriteEndpoint() {
        String executionUrl = "/api/v1/environment/{environmentId}/service/{serviceId}/execution/{executionId}/audit"
                .replace("{environmentId}", environmentId)
                .replace("{serviceId}", serviceId)
                .replace("{executionId}", executionExpectation.getExecutionId());


        List<AuditEntryExpectation> auditEntryExpectations = executionExpectation.getAuditEntryExpectations();

        if (auditEntryExpectations.size() == 1) {

            AuditEntryExpectation request = auditEntryExpectations.get(0);
            wireMockServer.stubFor(post(urlPathEqualTo(executionUrl))
                    .withRequestBody(equalToJson(toJson(request), true, true))
                    .willReturn(aResponse()
                            .withStatus(201)
                            .withHeader("Content-Type", "application/json")
                    ));

        } else {
            String scenarioName = "audit-logs";
            for (int i = 0; i < auditEntryExpectations.size(); i++) {

                String scenarioState = i == 0
                        ? Scenario.STARTED
                        : "audit-log-state-" + i;

                AuditEntryExpectation request = auditEntryExpectations.get(i);

                String json = toJson(request);
                wireMockServer.stubFor(post(urlPathEqualTo(executionUrl))
                        .inScenario(scenarioName)
                        .whenScenarioStateIs(scenarioState)
                        .willSetStateTo("audit-log-state-" + (i + 1))
                        .withRequestBody(equalToJson(json, true, true))
                        .willReturn(aResponse()
                                .withStatus(201)
                                .withHeader("Content-Type", "application/json")
                        ));
            }
        }
    }

    private void mockReleaseLockEndpoint() {
        ExecutionPlanResponse.Lock lockResponse = new ExecutionPlanResponse.Lock();
        lockResponse.setKey(serviceId);
        lockResponse.setOwner(runnerId);
        lockResponse.setAcquiredForMillis(executionExpectation.getAcquiredForMillis());
        lockResponse.setAcquisitionId(DEFAULT_LOCK_ACQUISITION);

        String url = "/api/v1/{key}/lock".replace("{key}", serviceId);
        wireMockServer.stubFor(delete(urlPathEqualTo(url))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(toJson(lockResponse))
                ));
    }


    private ExecutionPlanResponse getExecutionPlanResponse(int index) {
        if (!(executionPlanResponses.get(index) instanceof FullExecutionPlanResponse)) {
            return executionPlanResponses.get(index);
        }
        ExecutionPlanResponse executionPlanResponse = new ExecutionPlanResponse();
        executionPlanResponse.setExecutionId(executionExpectation.executionId);
        executionPlanResponse.setAction(ExecutionPlanResponse.Action.EXECUTE);

        ExecutionPlanResponse.Lock lockMock = new ExecutionPlanResponse.Lock();
        lockMock.setKey(serviceId);
        lockMock.setOwner(runnerId);
        lockMock.setAcquiredForMillis(executionExpectation.getAcquiredForMillis());
        lockMock.setAcquisitionId(DEFAULT_LOCK_ACQUISITION);

        executionPlanResponse.setLock(lockMock);

        executionPlanResponse.setStages(Collections.singletonList(toStageResponse(executionExpectation.getStageRequest())));
        return executionPlanResponse;
    }

    private static ExecutionPlanResponse.Stage toStageResponse(StageRequest stageRequest) {
        ExecutionPlanResponse.Stage stage = new ExecutionPlanResponse.Stage();
        stage.setName(stageRequest.getName());
        stage.setTasks(stageRequest.getTasks().stream()
                .map(task -> new ExecutionPlanResponse.Task(task.getId()))
                .collect(Collectors.toList()));
        return stage;
    }


    private static class FullExecutionPlanResponse extends ExecutionPlanResponse {

    }

    private static class ExecutionExpectation {
        private final String executionId;
        private final StageRequest stageRequest;
        private final List<AuditEntryExpectation> auditEntryExpectations;
        private final long elapsedMillis;
        private final long acquiredForMillis;

        public ExecutionExpectation(String executionId,
                                    StageRequest stageRequest,
                                    List<AuditEntryExpectation> auditEntryExpectations,
                                    long acquiredForMillis,
                                    long elapsedMillis) {
            this.executionId = executionId;
            this.stageRequest = stageRequest;
            this.auditEntryExpectations = auditEntryExpectations;
            this.acquiredForMillis = acquiredForMillis;
            this.elapsedMillis = elapsedMillis;
        }

        public String getExecutionId() {
            return executionId;
        }

        public List<AuditEntryExpectation> getAuditEntryExpectations() {
            return auditEntryExpectations;
        }

        public StageRequest getStageRequest() {
            return stageRequest;
        }

        public long getElapsedMillis() {
            return elapsedMillis;
        }

        public long getAcquiredForMillis() {
            return acquiredForMillis;
        }
    }

}
