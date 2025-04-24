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

package io.flamingock.core.task.loaded;

import io.flamingock.commons.utils.ReflectionUtil;
import io.flamingock.core.api.annotations.Execution;
import io.flamingock.core.api.annotations.RollbackExecution;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Optional;
import java.util.StringJoiner;

public class CodeLoadedChangeUnit extends AbstractLoadedChangeUnit {
    CodeLoadedChangeUnit(String id,
                         String order,
                         Class<?> source,
                         boolean runAlways,
                         boolean transactional,
                         boolean newChangeUnit,
                         boolean systemTask) {
        super(id, order, source, runAlways, transactional, newChangeUnit, systemTask);
    }

    @Override
    public Method getExecutionMethod() {
        Optional<Method> firstAnnotatedMethod = ReflectionUtil.findFirstAnnotatedMethod(getSourceClass(), Execution.class);
        return firstAnnotatedMethod
                .orElseThrow(() -> new IllegalArgumentException(String.format(
                        "Executable changeUnit[%s] without execution method annotated with: %s",
                        getSource(),
                        Execution.class.getName())));
    }

    @Override
    public Optional<Method> getRollbackMethod() {
        return ReflectionUtil.findFirstAnnotatedMethod(getSourceClass(), RollbackExecution.class);
    }

    @Override
    public String pretty() {
        String fromParent = super.pretty();
        return fromParent + String.format("\n\t\t[class: %s]", getSource());
    }


    @Override
    public String toString() {
        return new StringJoiner(", ", CodeLoadedChangeUnit.class.getSimpleName() + "[", "]")
                .add("source=" + source)
                .add("sourceClass=" + getSource())
                .add("sourceName='" + getSource() + "'")
                .add("id='" + getId() + "'")
                .add("runAlways=" + isRunAlways())
                .add("transactional=" + isTransactional())
                .add("order=" + getOrder())
                .add("sortable=" + isSortable())
                .toString();
    }
}
