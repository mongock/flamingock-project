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

package io.flamingock.core.cloud.planner.client;

import io.flamingock.core.cloud.auth.AuthManager;
import io.flamingock.core.cloud.planner.ExecutionPlanResponse;
import io.flamingock.core.configurator.core.ServiceId;
import io.flamingock.core.cloud.planner.ExecutionPlanRequest;
import io.flamingock.core.runner.RunnerId;
import io.flamingock.core.util.http.Http;

public class HttpExecutionPlannerClient implements ExecutionPlannerClient {


    private final String SERVICE_PARAM = "service";

    private final Http.RequestBuilder requestBuilder;

    private final String pathTemplate;

    private final AuthManager authManager;



    public HttpExecutionPlannerClient(String host,
                                      String apiVersion,
                                      Http.RequestBuilderFactory httpFactoryBuilder,
                                      AuthManager authManager) {
        this.pathTemplate = "/api/v1/environment/qa/service/invoices/execution";//String.format("/api/%s/{%s}/execution", apiVersion, SERVICE_PARAM);
        this.requestBuilder = httpFactoryBuilder
                .getRequestBuilder(host);
        this.authManager = authManager;
    }

    //TODO add environment
    @Override
    public ExecutionPlanResponse createExecution(ServiceId serviceId,
                                                 RunnerId runnerId,
                                                 ExecutionPlanRequest request,
                                                 String lastAcquisitionId,
                                                 long elapsedMillis) {
        return requestBuilder
                .POST(pathTemplate)
                .withRunnerId(runnerId)
                .withBearerToken(authManager.getJwtToken())
                .addPathParameter(SERVICE_PARAM, serviceId.toString())
                .addQueryParameter("lastAcquisitionId", lastAcquisitionId)
                .addQueryParameter("elapsedMillis", elapsedMillis)
                .setBody(request)
                .execute(ExecutionPlanResponse.class);
    }
}
