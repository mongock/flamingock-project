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

import io.flamingock.core.configurator.core.ServiceId;
import io.flamingock.core.engine.audit.AuditWriter;
import io.flamingock.core.engine.audit.writer.AuditEntry;
import io.flamingock.core.runner.RunnerId;
import io.flamingock.core.util.Result;
import io.flamingock.core.util.http.Http;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HtttpAuditWriter implements AuditWriter {

    private static final Logger logger = LoggerFactory.getLogger(HtttpAuditWriter.class);

    private final String SERVICE_PARAM = "service";

    private final Http.RequestBuilder requestBuilder;

    private final String pathTemplate;

    private final RunnerId runnerId;

    private final ServiceId serviceId;

    public HtttpAuditWriter(String host,
                            ServiceId serviceId,
                            RunnerId runnerId,
                            String apiVersion,
                            Http.RequestBuilderFactory requestBuilderFactory) {
        this.serviceId = serviceId;
        this.runnerId = runnerId;
        this.pathTemplate = String.format("/%s/{%s}/audit", apiVersion, SERVICE_PARAM);
        this.requestBuilder = requestBuilderFactory.getRequestBuilder(host);
    }

    @Override
    public Result writeEntry(AuditEntry auditEntry) {
        try {
            requestBuilder
                    .POST(pathTemplate)
                    .addPathParameter(SERVICE_PARAM, serviceId.toString())
                    .withRunnerId(runnerId)
                    .setBody(auditEntry)
                    .execute();
            return Result.OK();
        } catch (Throwable throwable) {
            logger.error("Error writing audit [{}]  :\n{}", auditEntry.getChangeId(), throwable.toString());
            return new Result.Error(throwable);
        }

    }


}