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

/**
 * <p>Repository interface to manage lock in database, which will be used by LockManager</p>
 */
public interface LockRepository {


    /**
     * a) If there is an existing lock in the database for the same key and owner {@code (existingLock.key==newLock.key &&
     * existingLoc.owner==newLock.owner)}, then the lock in database is updated/refreshed with the new values. The most
     * common scenario is to extend the lock's expiry date.
     * <p>
     * b) If there is an existing lock in the database for the same key and different owner, but expired {@code (existingLock.key==newLock.key &&
     * existingLock.owner!=newLock.owner && now > expiredAt)}, the lock is replaced with the newLock, so the owner of the lock for
     * that key is newLock.owner
     * <p>
     * c) If scenario b, but lock is not expired yet, should throw an LockPersistenceException.
     * <p>
     * d) If there isn't any lock with key=newLock.key, newLock is inserted.
     *
     * @param newLock lock to replace the existing one or be inserted.
     * @throws LockRepositoryException if there is a lock in database with same key, but is expired and belong to
     *                                  another owner or cannot insert/update the lock for any other reason
     */
    void upsert(LockEntry newLock) throws LockRepositoryException;

    /**
     * The only goal of this method is to update(mainly to extend the expiry date) the lock in case is already owned. So
     * it requires a Lock for the same key and owner {@code (existingLock.key==newLock.key && existingLoc.owner==newLock.owner)}.
     * <p>
     * If there is no lock for the key or it doesn't belong to newLock.owner, a LockPersistenceException is thrown.
     * <p>
     * Take into account that if it's already expired, but still with the same owner, we are lucky, no one has taken it yet,
     * so we can still extend the expiration time.
     *
     * @param newLock lock to replace the existing one or be inserted.
     * @throws LockRepositoryException if there is a lock in database with same key, but is expired and belong to
     *                                  another owner or cannot insert/update the lock for any other reason
     */
    void updateOnlyIfSameOwner(LockEntry newLock) throws LockRepositoryException;

    /**
     * Retrieves a lock by key
     *
     * @param lockKey key
     * @return LockEntry
     */
    //TODO Optional
    LockEntry findByKey(String lockKey);

    /**
     * Removes from database all the locks with the same key(only can be one) and owner
     *
     * @param lockKey lock key
     * @param owner   lock owner
     */
    void removeByKeyAndOwner(String lockKey, String owner);

    /**
     * Only for testing
     */
    void deleteAll();

}
