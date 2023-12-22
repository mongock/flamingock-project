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

package io.flamingock.core.cloud.transaction;

import io.flamingock.core.engine.audit.domain.AuditItem;
import io.flamingock.core.task.executable.ExecutableTask;

import java.util.Optional;

public interface CloudLocalStater {

    /**
     * Retrieves the current status form the database
     * @return current status
     */
    Optional<CloudLocalStatus> getStatus();

    /**
     * Removes any status from the database
     */
    void cleanStatus();

    /**
     * Upsert operation for new status in database. If there is an existing one, it gets overwritten
     * @param status the status to be written
     */
    void setStatus(CloudLocalStatus status);

    default void setOngoingExecution(ExecutableTask ongoingTask) {
        setStatus(new CloudLocalStatus(ongoingTask.getDescriptor().getId(), AuditItem.Operation.EXECUTION));
    }

    default void setOngoingRollback(ExecutableTask ongoingTask) {
        setStatus(new CloudLocalStatus(ongoingTask.getDescriptor().getId(), AuditItem.Operation.ROLLBACK));
    }
}
