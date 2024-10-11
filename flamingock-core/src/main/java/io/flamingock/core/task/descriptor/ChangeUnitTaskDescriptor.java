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

import java.lang.reflect.Method;
import java.util.Optional;
import java.util.StringJoiner;

public class ChangeUnitTaskDescriptor extends ReflectionTaskDescriptor {

    private final boolean isNewChangeUnit;

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
                    "Task class[%s] should be annotate with %s or %s",
                    source.getName(),
                    ChangeUnit.class.getName(),
                    io.mongock.api.annotations.ChangeUnit.class.getName()
            ));
        }
    }

    public ChangeUnitTaskDescriptor(String id, String order, Class<?> source, boolean runAlways, boolean transactional, boolean isNewChangeUnit) {
        super(id, order, source, runAlways, transactional);
        this.isNewChangeUnit = isNewChangeUnit;
    }


    public Method getExecutionMethod() {
        if(isNewChangeUnit) {
            return ReflectionUtil.findFirstAnnotatedMethod(getSourceClass(), Execution.class)
                    .orElseThrow(() -> new IllegalArgumentException(String.format(
                            "ExecutableChangeUnit[%s] without %s method",
                            getSourceClass().getName(),
                            Execution.class.getSimpleName())));
        } else {
            return ReflectionUtil.findFirstAnnotatedMethod(getSourceClass(), io.mongock.api.annotations.Execution.class)
                    .orElseThrow(() -> new IllegalArgumentException(String.format(
                            "ExecutableChangeUnit[%s] without %s method",
                            getSourceClass().getName(),
                            io.mongock.api.annotations.Execution.class.getSimpleName())));
        }
    }

    public Optional<Method> getRollbackMethod() {
        if(isNewChangeUnit) {
            return ReflectionUtil.findFirstAnnotatedMethod(getSourceClass(), RollbackExecution.class);
        } else {
            return ReflectionUtil.findFirstAnnotatedMethod(getSourceClass(), io.mongock.api.annotations.RollbackExecution.class);
        }
    }

    public Optional<Method> getBeforeExecutionMethod() {
        return isNewChangeUnit ? Optional.empty() : ReflectionUtil.findFirstAnnotatedMethod(getSourceClass(), BeforeExecution.class);
    }

    public Optional<Method> getRollbackBeforeExecutionMethod() {
        return isNewChangeUnit ? Optional.empty() : ReflectionUtil.findFirstAnnotatedMethod(getSourceClass(), RollbackBeforeExecution.class);
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
