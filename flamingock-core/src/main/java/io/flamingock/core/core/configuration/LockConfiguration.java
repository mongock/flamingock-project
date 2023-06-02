package io.flamingock.core.core.configuration;

import static io.flamingock.core.core.util.Constants.*;

public class LockConfiguration {

    /**
     * The period the lock will be reserved once acquired.
     * If it finishes before, it will release it earlier.
     * If the process takes longer thant this period, it will be automatically extended.
     * Default 1 minute.
     * Minimum 3 seconds.
     */
    private long lockAcquiredForMillis = DEFAULT_LOCK_ACQUIRED_FOR_MILLIS;

    /**
     * The time after what Mongock will quit trying to acquire the lock in case it's acquired by another process.
     * Default 3 minutes.
     * Minimum 0, which means won't wait whatsoever.
     */
    private long lockQuitTryingAfterMillis = DEFAULT_QUIT_TRYING_AFTER_MILLIS;

    /**
     * In case the lock is held by another process, it indicates the frequency to try to acquire it.
     * Regardless of this value, the longest Mongock will wait if until the current lock's expiration.
     * Default 1 second.
     * Minimum 500 millis.
     */
    private long lockTryFrequencyMillis = DEFAULT_TRY_FREQUENCY_MILLIS;

    /**
     * Mongock will throw MongockException if lock can not be obtained. Default true
     */
    private boolean throwExceptionIfCannotObtainLock = true;



    public void setLockAcquiredForMillis(long lockAcquiredForMillis) {
        this.lockAcquiredForMillis = lockAcquiredForMillis;
    }

    public void setLockQuitTryingAfterMillis(Long lockQuitTryingAfterMillis) {
        this.lockQuitTryingAfterMillis = lockQuitTryingAfterMillis;
    }

    public void setLockTryFrequencyMillis(long lockTryFrequencyMillis) {
        this.lockTryFrequencyMillis = lockTryFrequencyMillis;
    }

    public void setThrowExceptionIfCannotObtainLock(boolean throwExceptionIfCannotObtainLock) {
        this.throwExceptionIfCannotObtainLock = throwExceptionIfCannotObtainLock;
    }


    public long getLockAcquiredForMillis() {
        return lockAcquiredForMillis;
    }

    public Long getLockQuitTryingAfterMillis() {
        return lockQuitTryingAfterMillis;
    }

    public long getLockTryFrequencyMillis() {
        return lockTryFrequencyMillis;
    }

    public boolean isThrowExceptionIfCannotObtainLock() {
        return throwExceptionIfCannotObtainLock;
    }
}
