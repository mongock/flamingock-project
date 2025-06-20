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

package io.flamingock.cloud.auth;

import io.flamingock.internal.util.http.Http;
import io.flamingock.internal.common.cloud.auth.AuthRequest;
import io.flamingock.internal.common.cloud.auth.AuthResponse;

public class HttpAuthClient implements AuthClient {


    private final Http.RequestBuilder requestBuilder;

    private final String pathTemplate;

    public HttpAuthClient(String host,
                          String apiVersion,
                          Http.RequestBuilderFactory httpFactoryBuilder) {
        this.pathTemplate = String.format("/api/%s/auth/exchange-token", apiVersion);
        this.requestBuilder = httpFactoryBuilder.getRequestBuilder(host);
    }

    @Override
    public AuthResponse getToken(AuthRequest request) {
        return requestBuilder
                .POST(pathTemplate)
                .setBody(request)
                .execute(AuthResponse.class);
    }
}
