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

package io.flamingock.internal.core.cloud.transaction;

import io.flamingock.internal.common.cloud.vo.OngoingStatus;
import io.flamingock.internal.core.task.executable.ExecutableTask;
import java.util.Set;

public interface OngoingStatusRepository {

    /**
     * Retrieves the current status form the database
     *
     * @return non-null set of ongoing statuses
     */
    Set<TaskWithOngoingStatus> getOngoingStatuses();

    /**
     * Idempotently removes any ongoing status for the given taskId from the local database.
     * If the operation cannot be performed, throws a RuntimeException
     *
     * @param taskId taskId for which the statuses need to be removed
     */
    void cleanOngoingStatus(String taskId);

    /**
     * Upsert operation for new status in database. If there is an existing one, it gets overwritten
     *
     * @param status the status to be written
     */
    void saveOngoingStatus(TaskWithOngoingStatus status);

    default void setOngoingExecution(ExecutableTask ongoingTask) {
        saveOngoingStatus(new TaskWithOngoingStatus(ongoingTask.getId(), OngoingStatus.EXECUTION));
    }

    default void setOngoingRollback(ExecutableTask ongoingTask) {
        saveOngoingStatus(new TaskWithOngoingStatus(ongoingTask.getId(), OngoingStatus.ROLLBACK));
    }
}
