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

package io.flamingock.core.task.descriptor;

import io.flamingock.commons.utils.ReflectionUtil;
import io.flamingock.core.api.annotations.ChangeUnit;
import io.flamingock.core.api.annotations.Execution;
import io.flamingock.core.api.annotations.RollbackExecution;
import io.flamingock.core.utils.ExecutionUtils;
import io.mongock.api.annotations.BeforeExecution;
import io.mongock.api.annotations.RollbackBeforeExecution;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.Optional;
import java.util.StringJoiner;

public class ChangeUnitTaskDescriptor extends AbstractChangeUnitTaskDescriptor {
    private static final Logger logger = LoggerFactory.getLogger(ChangeUnitTaskDescriptor.class);

    public static ChangeUnitTaskDescriptor fromClass(Class<?> source) {
        if (ExecutionUtils.isNewChangeUnit(source)) {
            ChangeUnit changeUnitAnnotation = source.getAnnotation(ChangeUnit.class);
            return new ChangeUnitTaskDescriptor(
                    changeUnitAnnotation.id(),
                    changeUnitAnnotation.order(),
                    source,
                    changeUnitAnnotation.runAlways(),
                    changeUnitAnnotation.transactional(),
                    true);
        } else if (ExecutionUtils.isLegacyChangeUnit(source)) {
            logger.warn("Detected legacy changeUnit[{}]. If it's an old changeUnit created for Mongock, it's fine. " +
                            "Otherwise, it's highly recommended us the new API[in package {}]",
                    source.getName(),
                    "io.flamingock.core.api.annotations");
            io.mongock.api.annotations.ChangeUnit changeUnitAnnotation = source.getAnnotation(io.mongock.api.annotations.ChangeUnit.class);
            return new ChangeUnitTaskDescriptor(
                    changeUnitAnnotation.id(),
                    changeUnitAnnotation.order(),
                    source,
                    changeUnitAnnotation.runAlways(),
                    changeUnitAnnotation.transactional(),
                    false);
        } else {
            throw new IllegalArgumentException(String.format(
                    "Task class[%s] should be annotate with %s",
                    source.getName(),
                    ChangeUnit.class.getName()
            ));
        }
    }

    public ChangeUnitTaskDescriptor(String id, String order, Class<?> source, boolean runAlways, boolean transactional, boolean isNewChangeUnit) {
        super(id, order, source, runAlways, transactional, isNewChangeUnit);
    }

    @Override
    public Method getExecutionMethod() {
        if (isNewChangeUnit()) {
            return ReflectionUtil.findFirstAnnotatedMethod(getSourceClass(), Execution.class)
                    .orElseThrow(() -> new IllegalArgumentException(String.format(
                            "Executable changeUnit[%s] without %s method",
                            getSourceClass().getName(),
                            Execution.class.getName())));
        } else {
            Optional<Method> legacyExecutionMethod = ReflectionUtil.findFirstAnnotatedMethod(getSourceClass(), io.mongock.api.annotations.Execution.class);
            if (!legacyExecutionMethod.isPresent()) {
                if (ReflectionUtil.findFirstAnnotatedMethod(getSourceClass(), Execution.class).isPresent()) {
                    throw new IllegalArgumentException(String.format(
                            "You are using new API for Execution annotation in your changeUnit class[%s], however your class is annotated with legacy ChangeUnit annotation[%s]. " +
                                    "It's highly recommended to use the new API[in package %s], unless it's a legacy changeUnit created with Mongock",
                            getSourceClass().getName(),
                            io.mongock.api.annotations.Execution.class.getName(),
                            "io.flamingock.core.api.annotations"));
                }
            }
            return legacyExecutionMethod
                    .orElseThrow(() -> new IllegalArgumentException(String.format(
                            "Your changeUnit class[%s] doesn't contain execution method.\n" +
                                    "It's highly recommended to use the new API[in package %s].\n" +
                                    "In case it's an legacy changeUnit created with Mongock, please add the execution method annotated with legacy API[%s] ",
                            getSourceClass().getName(),
                            "io.flamingock.core.api.annotations",
                            io.mongock.api.annotations.Execution.class.getName())));
        }
    }

    @Override
    public Optional<Method> getRollbackMethod() {
        if (isNewChangeUnit()) {
            Optional<Method> firstAnnotatedMethod = ReflectionUtil.findFirstAnnotatedMethod(getSourceClass(), RollbackExecution.class);
            if (!firstAnnotatedMethod.isPresent()) {
                if (ReflectionUtil.findFirstAnnotatedMethod(getSourceClass(), io.mongock.api.annotations.RollbackExecution.class).isPresent()) {
                    throw new IllegalArgumentException(String.format(
                            "Executable changeUnit[%s] rollback method should be annotated with new API[%s], instead of legacy API[%s] ",
                            getSourceClass().getName(),
                            RollbackExecution.class.getName(),
                            io.mongock.api.annotations.RollbackExecution.class.getName()));
                }
            }
            return firstAnnotatedMethod;
        } else {
            Optional<Method> firstAnnotatedMethod = ReflectionUtil.findFirstAnnotatedMethod(getSourceClass(), io.mongock.api.annotations.RollbackExecution.class);
            if (!firstAnnotatedMethod.isPresent()) {
                if (ReflectionUtil.findFirstAnnotatedMethod(getSourceClass(), RollbackExecution.class).isPresent()) {
                    throw new IllegalArgumentException(String.format(
                            "You are using new API for RollbackExecution annotation in your changeUnit class[%s], however your class is annotated with legacy ChangeUnit annotation[%s]. " +
                                    "It's highly recommended to use the new API[in package %s], unless it's a legacy changeUnit created with Mongock",
                            getSourceClass().getName(),
                            io.mongock.api.annotations.RollbackExecution.class.getName(),
                            "io.flamingock.core.api.annotations"));
                }
            }
            return firstAnnotatedMethod;
        }
    }

    public Optional<Method> getBeforeExecutionMethod() {
        Optional<Method> beforeExecutionMethod = ReflectionUtil.findFirstAnnotatedMethod(getSourceClass(), BeforeExecution.class);
        if (isNewChangeUnit() && beforeExecutionMethod.isPresent()) {
            throw new IllegalArgumentException(String.format("You are using legacy annotation [%s] with new API. You should create an independent ChangeUnit for it",
                    BeforeExecution.class.getName()));
        }
        return isNewChangeUnit() ? Optional.empty() : beforeExecutionMethod;
    }

    public Optional<Method> getRollbackBeforeExecutionMethod() {
        Optional<Method> rollbackBeforeExecution = ReflectionUtil.findFirstAnnotatedMethod(getSourceClass(), RollbackBeforeExecution.class);
        if (isNewChangeUnit() && rollbackBeforeExecution.isPresent()) {
            throw new IllegalArgumentException(String.format("You are using legacy annotation [%s] with new API. You should create an independent ChangeUnit for it",
                    RollbackBeforeExecution.class.getName()));
        }
        return isNewChangeUnit() ? Optional.empty() : rollbackBeforeExecution;
    }


    @Override
    public String pretty() {
        String fromParent = super.pretty();
        return fromParent + String.format("\n\t\t[class: %s]", getSourceName());
    }


    @Override
    public String toString() {
        return new StringJoiner(", ", ChangeUnitTaskDescriptor.class.getSimpleName() + "[", "]")
                .add("source=" + source)
                .add("sourceClass=" + getSourceClass())
                .add("sourceName='" + getSourceName() + "'")
                .add("id='" + getId() + "'")
                .add("runAlways=" + isRunAlways())
                .add("transactional=" + isTransactional())
                .add("order=" + getOrder())
                .add("sortable=" + isSortable())
                .toString();
    }
}