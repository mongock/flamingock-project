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

package io.flamingock.core.cloud.audit;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import io.flamingock.core.cloud.auth.AuthManager;
import io.flamingock.core.configurator.core.ServiceId;
import io.flamingock.core.engine.audit.AuditWriter;
import io.flamingock.core.engine.audit.writer.AuditEntry;
import io.flamingock.core.runner.RunnerId;
import io.flamingock.core.util.Result;
import io.flamingock.core.util.http.Http;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Map;

import static com.fasterxml.jackson.databind.MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS;

public class HtttpAuditWriter implements AuditWriter {

    private static final Logger logger = LoggerFactory.getLogger(HtttpAuditWriter.class);

    private final String SERVICE_PARAM = "service";

    private final Http.RequestBuilder requestBuilder;

    private final String pathTemplate;

    private final RunnerId runnerId;

    private final ServiceId serviceId;
    private final AuthManager authManager;

    public HtttpAuditWriter(String host,
                            ServiceId serviceId,
                            RunnerId runnerId,
                            String apiVersion,
                            Http.RequestBuilderFactory requestBuilderFactory,
                            AuthManager authManager) {
        this.serviceId = serviceId;
        this.runnerId = runnerId;
        this.pathTemplate = String.format("/api/%s/{%s}/audit", apiVersion, SERVICE_PARAM);
        this.requestBuilder = requestBuilderFactory.getRequestBuilder(host);
        this.authManager  = authManager;
    }

    @Override
    public Result writeEntry(AuditEntry auditEntry) {
        try {
            AuditEntryRequest auditEntryRequest = buildRequest(auditEntry);
            requestBuilder
                    .POST(pathTemplate)
                    .withRunnerId(runnerId)
                    .withBearerToken(authManager.getJwtToken())
                    .addPathParameter(SERVICE_PARAM, serviceId.toString())
                    .setBody(auditEntryRequest)
                    .execute();
            return Result.OK();
        } catch (Throwable throwable) {
            logger.error("Error writing audit [{}]  :\n{}", auditEntry.getTaskId(), throwable.toString());
            return new Result.Error(throwable);
        }

    }

    private AuditEntryRequest buildRequest(AuditEntry auditEntry) {
        return new AuditEntryRequest(
                auditEntry.getExecutionPlanId(),
                auditEntry.getStageId(),
                auditEntry.getTaskId(),
                auditEntry.getAuthor(),
                ZonedDateTime.of(auditEntry.getCreatedAt(), ZoneId.systemDefault()).toInstant().toEpochMilli(),
                auditEntry.getState(),
                auditEntry.getType(),
                auditEntry.getClassName(),
                auditEntry.getMethodName(),
                auditEntry.getExecutionMillis(),
                auditEntry.getExecutionHostname(),
                auditEntry.getMetadata(),
                auditEntry.getSystemChange(),
                auditEntry.getErrorTrace()
        );
    }


}
