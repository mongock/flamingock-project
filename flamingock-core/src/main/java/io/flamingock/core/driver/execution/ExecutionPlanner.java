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

package io.flamingock.core.driver.execution;

import io.flamingock.core.driver.lock.LockAcquisition;
import io.flamingock.core.driver.lock.LockException;
import io.flamingock.core.driver.lock.LockOptions;
import io.flamingock.core.pipeline.LoadedStage;
import io.flamingock.core.pipeline.Pipeline;

public interface ExecutionPlanner {

    default LockAcquisition getNextExecution(LoadedStage loadedStage) throws LockException {
        return acquiredIfRequired(loadedStage, LockOptions.builder().build());
    }

    default Execution getNextExecution(Pipeline pipeline) throws LockException {
        return getNextExecution(pipeline, LockOptions.builder().build());
    }

    /**
     * Acquire the lock if available and if there is any outstanding work, based on the stage description passed as
     * parameter.
     * It's intended to be the first acquisition. Once taken(if required), it just needs to be extended(ensured).
     * In case there is pending work to do, but the lock is hold by other process, it blocks until it's acquired
     * or until the retry policy is exceeded.
     *
     * @param loadedProcess stage description from the filesystem
     * @return LockAcquisition.Acquired if required and acquired successfully, or LockAcquisition.NotRequired not required
     * @throws LockException if the lock is required and cannot be acquired within the configured margin(retry, etc.)
     */
    /**
     *
     * @param loadedStage
     * @param options
     * @return
     * @throws LockException
     */
    LockAcquisition acquiredIfRequired(LoadedStage loadedStage, LockOptions options) throws LockException;


    Execution getNextExecution(Pipeline pipeline, LockOptions lockOptions) throws LockException;
}
