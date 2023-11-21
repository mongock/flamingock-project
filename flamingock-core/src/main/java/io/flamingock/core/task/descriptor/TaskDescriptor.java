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

import java.util.Optional;

public interface TaskDescriptor extends Comparable<TaskDescriptor> {

    String getId();

    boolean isRunAlways();

    boolean isTransactional();

    String getSourceName();

    Optional<String> getOrder();

    default String pretty() {
        return getOrder().isPresent() ? String.format("%s) %s ", getOrder().get(), getId()) : String.format(" %s ", getId());
    }

    default boolean isSortable() {
        return getOrder().isPresent();
    }

    @Override
    default int compareTo(TaskDescriptor other) {
        if (!other.getOrder().isPresent()) {
            return -1;
        } else if (!this.getOrder().isPresent()) {
            return 1;
        } else {
            return this.getOrder().get().compareTo(other.getOrder().get());
        }
    }

}