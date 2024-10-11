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

package io.flamingock.core.task.executable.template;

import io.flamingock.commons.utils.ReflectionUtil;
import io.flamingock.core.engine.audit.writer.AuditEntry;
import io.flamingock.core.task.descriptor.TaskDescriptor;
import io.flamingock.core.task.descriptor.TemplatedTaskDescriptor;
import io.flamingock.core.task.executable.ExecutableTaskFactory;
import io.flamingock.core.task.navigation.navigator.StepNavigator;
import io.flamingock.template.annotations.TemplateConfigSetter;
import io.flamingock.template.annotations.TemplateConfigValidator;
import io.flamingock.template.annotations.TemplateExecution;
import io.flamingock.template.annotations.TemplateRollbackExecution;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;


/**
 * Factory for ChangeUnit classes
 */
public class TemplatedExecutableTaskFactory implements ExecutableTaskFactory<TemplatedTaskDescriptor> {

    private static final Logger logger = LoggerFactory.getLogger(StepNavigator.class);

    @Override
    public List<TemplatedExecutableTask> extractTasks(String stageName, TemplatedTaskDescriptor descriptor, AuditEntry.Status initialState) {
        return Collections.singletonList(getTasksFromReflection(stageName, descriptor, initialState));
    }

    private TemplatedExecutableTask getTasksFromReflection(String stageName,
                                                           TemplatedTaskDescriptor taskDescriptor,
                                                           AuditEntry.Status initialState) {

        Method executionMethod = ReflectionUtil.findFirstAnnotatedMethod(taskDescriptor.getSourceClass(), TemplateExecution.class)
                .orElseThrow(() -> new IllegalArgumentException(String.format(
                        "Templated[%s] without %s method",
                        taskDescriptor.getSourceClass().getName(),
                        TemplateExecution.class.getSimpleName())));
        Optional<Method> rollbackMethod = getRollbackMethodOpt(taskDescriptor);

        Optional<Method> configurationSetterOpt = ReflectionUtil
                .findFirstAnnotatedMethod(taskDescriptor.getSourceClass(), TemplateConfigSetter.class);


        Optional<Method> configurationValidatorOpt = ReflectionUtil
                .findFirstAnnotatedMethod(taskDescriptor.getSourceClass(), TemplateConfigValidator.class);


        return new TemplatedExecutableTask(
                stageName,
                taskDescriptor,
                AuditEntry.Status.isRequiredExecution(initialState),
                executionMethod,
                rollbackMethod.orElse(null),
                configurationSetterOpt.orElse(null),
                configurationValidatorOpt.orElse(null)
        );

    }

    private static Optional<Method> getRollbackMethodOpt(TemplatedTaskDescriptor taskDescriptor) {
        Optional<Method> rollbackMethodOpt = ReflectionUtil
                .findFirstAnnotatedMethod(taskDescriptor.getSourceClass(), TemplateRollbackExecution.class);
        Optional<Method> rollbackMethod;
        if (rollbackMethodOpt.isPresent()) {
            Method potentialRollbackMethod = rollbackMethodOpt.get();
            TemplateRollbackExecution rollbackExecutionAnnotation = potentialRollbackMethod.getAnnotation(TemplateRollbackExecution.class);
            String[] conditionalOnAllConfigurationPropertiesNotNull = rollbackExecutionAnnotation.conditionalOnAllConfigurationPropertiesNotNull();
            if (conditionalOnAllConfigurationPropertiesNotNull == null || conditionalOnAllConfigurationPropertiesNotNull.length == 0) {
                rollbackMethod = Optional.of(potentialRollbackMethod);
            } else {
                Map<String, Object> configMap = taskDescriptor.getTemplateConfiguration();
                if (Arrays.stream(conditionalOnAllConfigurationPropertiesNotNull).allMatch(configMap::containsKey)) {
                    rollbackMethod = Optional.of(potentialRollbackMethod);
                } else {
                    rollbackMethod = Optional.empty();
                }
            }

        } else {
            rollbackMethod = Optional.empty();
        }
        return rollbackMethod;
    }


}