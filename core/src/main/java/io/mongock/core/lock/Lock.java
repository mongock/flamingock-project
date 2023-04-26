package io.mongock.core.lock;


import static io.mongock.core.lock.LockStatus.ACQUIRED;
import static io.mongock.core.lock.LockStatus.NOT_REQUIRED;

public interface Lock extends AutoCloseable {


    /**
     * Ensures the lock is taken for enough period and extends it if required.
     * It's intended to be used after the lock is acquired, to ensure it's kept.
     * It does NOT newly acquire it, in case it's not already acquired.
     */
    void ensureLock();

    LockStatus getStatus();


    default boolean isAcquired() {
        return getStatus() == ACQUIRED;
    }

    default boolean isRequired() {
        return getStatus() != NOT_REQUIRED;
    }

    default void release() {
        try {
            close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
