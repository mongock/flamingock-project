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

package io.flamingock.importer.cloud.common;

import io.flamingock.core.api.CloudSystemModule;

import java.util.Collection;
import java.util.Collections;
import java.util.List;


public interface Importer extends CloudSystemModule {
    List<Class<?>> TASK_CLASSES = Collections.singletonList(
            ImporterChangeUnit.class
    );

    public final static String DEFAULT_MONGOCK_REPOSITORY_NAME = "mongockChangeLog";

    @Override
    default int getOrder() {
        return 0;
    }

    @Override
    default String getName() {
        return "importer";
    }

    @Override
    default Collection<Class<?>> getTaskClasses() {
        return TASK_CLASSES;
    }

    @Override
    default boolean isBeforeUserStages() {
        return true;
    }
}
