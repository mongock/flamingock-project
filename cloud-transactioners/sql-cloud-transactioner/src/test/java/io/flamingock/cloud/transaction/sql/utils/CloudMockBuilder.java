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

package io.flamingock.cloud.transaction.sql.utils;

import io.flamingock.core.cloud.planner.ExecutionPlanResponse;
import io.flamingock.core.util.http.Http;
import org.mockito.MockedStatic;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public final class CloudMockBuilder {


    private String serviceName = "test-service";

    private String runnerId = "owner-it's-not-checked";


    private MockedStatic<Http> http;

    private final List<ExecutionPlanResponse> executionPlanResponses = new ArrayList<>();


    private Http.RequestWithBody requestWithBody;

    private Http.Request basicRequest;


    public CloudMockBuilder setServiceName(String serviceName) {
        this.serviceName = serviceName;
        return this;
    }

    public CloudMockBuilder setRunnerId(String runnerId) {
        this.runnerId = runnerId;
        return this;
    }

    public CloudMockBuilder setHttp(MockedStatic<Http> http) {
        this.http = http;
        return this;
    }

    public CloudMockBuilder addContinueExecutionPlanResponse() {
        ExecutionPlanResponse executionPlanResponse = new ExecutionPlanResponse();
        executionPlanResponse.setAction(ExecutionPlanResponse.Action.CONTINUE);
        executionPlanResponses.add(executionPlanResponse);
        return this;
    }

    public CloudMockBuilder addAwaitExecutionPlanResponse(long acquiredForMillis) {
        ExecutionPlanResponse executionPlanResponse = new ExecutionPlanResponse();
        executionPlanResponse.setAction(ExecutionPlanResponse.Action.AWAIT);
        ExecutionPlanResponse.Lock lock = new ExecutionPlanResponse.Lock();
        lock.setAcquisitionId(UUID.randomUUID().toString());
        lock.setKey(serviceName);
        lock.setOwner(runnerId);
        lock.setAcquiredForMillis(acquiredForMillis);
        executionPlanResponse.setLock(lock);
        executionPlanResponses.add(executionPlanResponse);
        return this;
    }

    public CloudMockBuilder addSingleExecutionPlanResponse(String stageName, String... tasks) {
        ExecutionPlanResponse executionPlanResponse = new ExecutionPlanResponse();
        executionPlanResponse.setAction(ExecutionPlanResponse.Action.EXECUTE);

        ExecutionPlanResponse.Lock lockMock = new ExecutionPlanResponse.Lock();
        lockMock.setKey(serviceName);
        lockMock.setOwner(runnerId);
        lockMock.setAcquiredForMillis(300L);
        lockMock.setAcquisitionId(UUID.randomUUID().toString());

        executionPlanResponse.setLock(lockMock);

        executionPlanResponse.setExecutionPlanId(UUID.randomUUID().toString());

        ExecutionPlanResponse.Stage stage1 = new ExecutionPlanResponse.Stage();
        stage1.setName(stageName);
        stage1.setTasks(Arrays.asList(tasks));
        List<ExecutionPlanResponse.Stage> stages = Collections.singletonList(stage1);
        executionPlanResponse.setStages(stages);
        executionPlanResponses.add(executionPlanResponse);

        return this;
    }


    public void mockServer() {

        Http.RequestBuilderFactory requestBuilderFactory = mock(Http.RequestBuilderFactory.class);
        Http.RequestBuilder requestBuilder = mock(Http.RequestBuilder.class);

        //POST
        requestWithBody = mock(Http.RequestWithBody.class);
        when(requestWithBody.withRunnerId(any())).thenReturn(requestWithBody);
        when(requestWithBody.addPathParameter(anyString(), any())).thenReturn(requestWithBody);
        when(requestWithBody.addQueryParameter(anyString(), any())).thenReturn(requestWithBody);
        when(requestWithBody.setBody(any())).thenReturn(requestWithBody);

        //DELETE
        basicRequest = mock(Http.Request.class);
        when(basicRequest.withRunnerId(any())).thenReturn(basicRequest);
        when(basicRequest.addPathParameter(anyString(), any())).thenReturn(basicRequest);
        when(basicRequest.addQueryParameter(anyString(), any())).thenReturn(basicRequest);

        //ExecutionPlanner client
        ExecutionPlanResponse firstItem = executionPlanResponses.get(0);
        if (executionPlanResponses.size() == 1) {
            when(requestWithBody.execute(ExecutionPlanResponse.class)).thenReturn(firstItem);
        } else {
            ExecutionPlanResponse[] restOfParameters = new ExecutionPlanResponse[executionPlanResponses.size() - 1];
            for(int i = 1 ; i < executionPlanResponses.size() ; i++) {
                restOfParameters[i-1] = executionPlanResponses.get(i);
            }
            when(requestWithBody.execute(ExecutionPlanResponse.class)).thenReturn(firstItem, restOfParameters);

        }

        when(requestBuilder.POST(anyString())).thenReturn(requestWithBody);
        when(requestBuilder.DELETE(anyString())).thenReturn(basicRequest);

        when(requestBuilderFactory.getRequestBuilder(anyString())).thenReturn(requestBuilder);

        http.when(() -> Http.builderFactory(any(), any())).thenReturn(requestBuilderFactory);
    }

    public Http.RequestWithBody getRequestWithBody() {
        return requestWithBody;
    }

    public Http.Request getBasicRequest() {
        return basicRequest;
    }
}
