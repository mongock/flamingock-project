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

package io.flamingock.core.runtime.dependency;

import io.flamingock.core.runtime.Dependency;
import io.flamingock.core.runtime.dependency.exception.ForbiddenParameterException;

import java.util.Optional;
import java.util.function.Predicate;

public abstract class AbstractDependencyContext implements DependencyContext {

    @Override
    public Optional<Dependency> getDependency(Class<?> type) throws ForbiddenParameterException {
        return getDependency(dependency -> type.isAssignableFrom(dependency.getType()));
    }

    @Override
    public Optional<Dependency> getDependency(String name) throws ForbiddenParameterException {
        if(name == null || name.isEmpty() || Dependency.DEFAULT_NAME.equals(name)) {
            throw new IllegalArgumentException("name cannot be null/empty  when retrieving dependency by name");
        }
        return getDependency(dependency -> name.equals(dependency.getName()));
    }

    private Optional<Dependency> getDependency(Predicate<Dependency> filter) throws ForbiddenParameterException {

        Optional<Dependency> dependencyOptional = getFromStorage(filter);
        if (!dependencyOptional.isPresent()) {
            return Optional.empty();

        }
        Dependency dependency = dependencyOptional.get();
        return DependencyBuildable.class.isAssignableFrom(dependency.getClass())
                ? getDependency(((DependencyBuildable) dependency).getImplType())
                : dependencyOptional;
    }


    protected abstract Optional<Dependency> getFromStorage(Predicate<Dependency> filter );

}
