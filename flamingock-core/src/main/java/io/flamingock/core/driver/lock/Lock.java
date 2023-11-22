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

package io.flamingock.core.driver.lock;


import java.time.LocalDateTime;

public interface Lock {

    /**
     * Ensures the lock is safely acquired(safely here means it's acquired with enough margin to operate),
     * or throws an exception otherwise.
     * <br />
     * In case the lock is about to expire, it will try to refresh it. In this scenario, the lock won't be considered
     * ensured until it's successfully extended. However, this scenario shouldn't happen, when a well configured daemon
     * is set up.
     *
     * @throws LockException if it cannot be ensured. Either is expired or, close to be expired and cannot be extended.
     */
    void ensure();

    /**
     * Refreshes the lock if it's already taken. Throws an exception otherwise.
     *
     * @return true if the lock has been successfully refreshed, or false if lock shouldn't be refreshed because it's in the middle
     * of a release process, or it's already released.
     * @throws LockException if there is any problem refreshing the lock or it's not acquired at all.
     */
    boolean refresh();


    /**
     * This should be called once all the process guarded by the lock(those who assumed the lock is ensured) has finished.
     * Otherwise, a race condition where a process A ensures the lock, starts a task that takes 2 seconds, but before finishing,
     * the lock is released(closed) and another instances acquire the lock, before process A has finished.
     */
    void release();

    /**
     * Return the lock's expiration time
     *
     * @return
     */
    LocalDateTime expiresAt();

    boolean isExpired();

}
