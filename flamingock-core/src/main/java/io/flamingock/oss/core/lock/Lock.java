package io.flamingock.oss.core.lock;


public interface Lock extends AutoCloseable {


    /**
     * Ensures the lock is taken for enough period and extends it if required.
     * It's intended to be used after the lock is acquired, to ensure it's kept.
     * It does NOT newly acquire it, in case it's not already acquired.
     */
    void ensureLock();

    LockStatus getStatus();


    default boolean isAcquired() {
        return getStatus() == LockStatus.ACQUIRED;
    }

    default boolean isRequired() {
        return getStatus() != LockStatus.NOT_REQUIRED;
    }

    default void release() {
        try {
            close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
