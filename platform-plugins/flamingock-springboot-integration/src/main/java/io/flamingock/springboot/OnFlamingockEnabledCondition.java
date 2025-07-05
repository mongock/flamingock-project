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

package io.flamingock.springboot;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.flamingock.api.SetupType;
import io.flamingock.internal.common.core.metadata.Constants;
import org.springframework.boot.autoconfigure.condition.ConditionMessage;
import org.springframework.boot.autoconfigure.condition.ConditionOutcome;
import org.springframework.boot.autoconfigure.condition.SpringBootCondition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.io.Resource;
import org.springframework.core.type.AnnotatedTypeMetadata;

import java.io.IOException;
import java.io.InputStream;

public class OnFlamingockEnabledCondition extends SpringBootCondition {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public ConditionOutcome getMatchOutcome(ConditionContext context, AnnotatedTypeMetadata metadata) {
        ConditionMessage.Builder message = ConditionMessage.forCondition("Flamingock Auto-configuration");
        
        // First check if flamingock is globally enabled
        String enabled = context.getEnvironment().getProperty("flamingock.enabled", "true");
        if (!"true".equalsIgnoreCase(enabled)) {
            return ConditionOutcome.noMatch(message.because("flamingock.enabled is set to false"));
        }

        // Check for pipeline metadata file
        Resource resource = context.getResourceLoader().getResource("classpath:" + Constants.FULL_PIPELINE_FILE_PATH);
        if (!resource.exists()) {
            return ConditionOutcome.noMatch(message.because("pipeline metadata file not found at " + Constants.FULL_PIPELINE_FILE_PATH));
        }

        try (InputStream inputStream = resource.getInputStream()) {
            JsonNode pipelineNode = objectMapper.readTree(inputStream);
            
            // Check if setup type is present and not BUILDER
            JsonNode setupNode = pipelineNode.get("setup");
            if (setupNode != null) {
                String setupType = setupNode.asText();
                if (SetupType.BUILDER.name().equals(setupType)) {
                    return ConditionOutcome.noMatch(message.because("setup type is BUILDER: automatic configuration disabled"));
                }
            }

            return ConditionOutcome.match(message.because("pipeline metadata found with default setup"));
            
        } catch (IOException e) {
            return ConditionOutcome.noMatch(message.because("failed to read pipeline metadata: " + e.getMessage()));
        }
    }
}