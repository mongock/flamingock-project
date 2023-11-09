/*
 * Copyright 2023 Flamingock (https://oss.flamingock.io)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.flamingock.core.pipeline.execution;

import java.util.Map;

public class StageExecutionContext {
    private final String executionId;

    private final String hostname;

    private final String author;

    private final Map<String, Object> metadata;


    public StageExecutionContext(String executionId, String hostname, String author, Map<String, Object> metadata) {
        this.executionId = executionId;
        this.hostname = hostname;
        this.author = author;
        this.metadata = metadata;
    }

    public String getExecutionId() {
        return executionId;
    }

    public String getHostname() {
        return hostname;
    }

    public String getAuthor() {
        return author;
    }

    public Map<String, Object> getMetadata() {
        return metadata;
    }
}
