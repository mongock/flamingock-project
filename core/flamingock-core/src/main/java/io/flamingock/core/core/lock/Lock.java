package io.flamingock.core.core.lock;


import java.time.LocalDateTime;

public interface Lock extends AutoCloseable {

    /**
     * Ensures the lock is safely acquired(safely here means its acquired with enough margin to operate),
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
    void close();

    /**
     * Return the lock's expiration time
     *
     * @return
     */
    LocalDateTime expiresAt();

    boolean isExpired();

}
