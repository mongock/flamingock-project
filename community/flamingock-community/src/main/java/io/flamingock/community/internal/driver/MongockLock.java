package io.flamingock.community.internal.driver;

import io.flamingock.community.internal.persistence.LockPersistenceException;
import io.flamingock.core.core.lock.Lock;
import io.flamingock.core.core.lock.LockException;
import io.flamingock.core.core.lock.LockStatus;
import io.flamingock.core.core.util.TimeService;
import io.flamingock.core.core.util.TimeUtil;
import io.flamingock.community.internal.persistence.LockEntry;
import io.flamingock.community.internal.persistence.LockRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

import static java.time.temporal.ChronoUnit.MILLIS;

public class MongockLock implements Lock {

    private static final Logger logger = LoggerFactory.getLogger(MongockLock.class);





    private static final String DEFAULT_KEY = "DEFAULT_KEY";
    public static final String LOG_EXPIRED_TEMPLATE = "Lock[{}] not refreshed at[{}] because the it's canceled/expired[{}]";


    //injections
    private final LockRepository lockRepository;

    private final TimeService timeService;

    private final String owner;

    private final long leaseMillis;

    private final long retryFrequencyMillis;

    private final long stopTryingAfterMillis;

    /**
     * It should never be null(after acquisition), just acquired(after now) or expired(before now)
     */
    private volatile LocalDateTime expiresAt;


    static Lock getLock(long leaseMillis,
                        long stopTryingAfterMillis,
                        long retryFrequencyMillis,
                        String owner,
                        LockRepository lockRepository,
                        TimeService timeService) {
        MongockLock lock = new MongockLock(leaseMillis, stopTryingAfterMillis, retryFrequencyMillis, owner, lockRepository, timeService);
        lock.acquire();
        return lock;

    }


    private MongockLock(long leaseMillis,
                        long stopTryingAfterMillis,
                        long retryFrequencyMillis,
                        String owner,
                        LockRepository lockRepository,
                        TimeService timeService) {
        this.leaseMillis = leaseMillis;
        this.stopTryingAfterMillis = stopTryingAfterMillis;
        this.retryFrequencyMillis = retryFrequencyMillis;
        this.owner = owner;
        this.lockRepository = lockRepository;
        this.timeService = timeService;
    }


    /**
     * This is supposed to be called just once, per lock, from the static method `getLock`
     *
     * @throws LockException if the lock cannot be acquired
     */
    private void acquire() throws LockException {
        Instant shouldStopTryingAt = timeService.nowPlusMillis(stopTryingAfterMillis);
        boolean keepLooping = true;
        do {
            try {
                logger.info("Flamingock trying to acquire the lock");
                synchronized (this) {
                    extendOrCreateNewLock(true);
                    keepLooping = false;
                }
            } catch (LockPersistenceException ex) {
                handleLockException(true, shouldStopTryingAt, ex);
            }

        } while (keepLooping);
        logger.info("Flamingock acquired the lock until: {}", expiresAt());
    }

    @Override
    public void ensure() throws LockException {
        logger.debug("Ensuring the lock");
        boolean ensured = false;
        Instant shouldStopTryingAt = timeService.nowPlusMillis(stopTryingAfterMillis);
        do {
            if (isExpired()) {
                throw new LockException(String.format(
                        "Lock not ensured at [%s] because the it's canceled/expired[%s]", timeService.currentDateTime(), expiresAt()
                ));
            }
            long margin = Math.max((long) (leaseMillis * 0.33/*30%*/), 1000L/*1sec*/);
            LocalDateTime threshold = expiresAt().minus(margin, MILLIS);
            if (timeService.currentDateTime().isAfter(threshold)) {
                try {
                    ensured = refresh();
                } catch (LockPersistenceException ex) {
                    handleLockException(false, shouldStopTryingAt, ex);
                }
            } else {
                logger.debug("Dont need to refresh the lock at[{}], it's acquired until: {}", timeService.currentDateTime(), expiresAt());
                ensured = true;
            }
        } while (!ensured);
    }

    @Override
    public boolean refresh() throws LockException {
        if (isExpired()) {
            logger.info(LOG_EXPIRED_TEMPLATE, owner, timeService.currentDateTime(), expiresAt());
            return false;
        }
        try {
            logger.debug("Flamingock trying to refresh the lock");
            synchronized (this) {
                if (isExpired()) {
                    logger.info(LOG_EXPIRED_TEMPLATE, owner, timeService.currentDateTime(), expiresAt());
                    return false;
                }
                extendOrCreateNewLock(false);
                logger.debug("Flamingock refreshed the lock until: {}", expiresAt());
                return true;
            }

        } catch (Exception ex) {
            throw new LockException(ex);
        }
    }

    @Override
    public void close() {
        logger.info("Flamingock waiting to release the lock");
        synchronized (this) {
            try {
                logger.info("Flamingock releasing the lock");
                logger.debug("Flamingock expiring the lock");
                setExpiresAt(LocalDateTime.now().minusDays(1));//forces expiring
                logger.debug("Flamingock removing the lock from database");
                lockRepository.removeByKeyAndOwner(DEFAULT_KEY, owner);
                logger.info("Flamingock released the lock");
            } catch (Exception ex) {
                logger.warn("Error removing the lock from database", ex);
            }
        }
    }

    private void extendOrCreateNewLock(boolean createNewLockIfNotExisting) throws LockPersistenceException {
        LocalDateTime expiresAt1 = timeService.currentDatePlusMillis(leaseMillis);
        final LockEntry newEntry = new LockEntry(
                DEFAULT_KEY,
                LockStatus.LOCK_HELD,
                owner,
                expiresAt1);

        if (createNewLockIfNotExisting) {
            lockRepository.upsert(newEntry);
        } else {
            lockRepository.updateOnlyIfSameOwner(newEntry);
        }
        setExpiresAt(newEntry.getExpiresAt());
    }

    @Override
    public LocalDateTime expiresAt() {
        return expiresAt;
    }


    private void setExpiresAt(LocalDateTime dateTime) {
        if (dateTime == null) {
            throw new IllegalArgumentException("expiresAt cannot be null");
        }
        expiresAt = dateTime;
    }

    @Override
    public boolean isExpired() {
        return timeService.isPast(expiresAt());
    }

    private void handleLockException(boolean acquiringLock, Instant shouldStopTryingAt, LockPersistenceException ex) {
        LockEntry currentLock = lockRepository.findByKey(DEFAULT_KEY);
        if (timeService.isPast(shouldStopTryingAt)) {
            throw new LockException(String.format(
                    "Quit trying lock after %s millis due to LockPersistenceException: \n\tcurrent lock:  %s\n\tnew lock: %s\n\tacquireLockQuery: %s\n\tdb error detail: %s",
                    stopTryingAfterMillis,
                    currentLock != null ? currentLock.toString() : "none",
                    ex.getNewLockEntity(),
                    ex.getAcquireLockQuery(),
                    ex.getDbErrorDetail()));
        }

        final boolean isLockOwnedByOtherProcess = currentLock != null && !currentLock.isOwner(owner);
        if (isLockOwnedByOtherProcess) {
            LocalDateTime currentLockExpiresAt = currentLock.getExpiresAt();
            logger.warn("Lock is taken by other process until: {}", currentLockExpiresAt);
            if (!acquiringLock) {
                throw new LockException(String.format(
                        "Lock held by other process. Cannot ensure lock.\n\tcurrent lock:  %s\n\tnew lock: %s\n\tacquireLockQuery: %s\n\tdb error detail: %s",
                        currentLock,
                        ex.getNewLockEntity(),
                        ex.getAcquireLockQuery(),
                        ex.getDbErrorDetail()));
            }
            waitForLock(currentLockExpiresAt);
        }

    }

    private void waitForLock(LocalDateTime expiresAt) {
        long currentMillis = timeService.currentMillis();
        long currentLockWillExpireInMillis = expiresAt.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli() - currentMillis;
        long sleepingMillis = retryFrequencyMillis;
        if (retryFrequencyMillis > currentLockWillExpireInMillis) {
            logger.info("The configured time frequency[{} millis] is higher than the current lock's expiration", retryFrequencyMillis);
            sleepingMillis = Math.max(currentLockWillExpireInMillis, 500L);//0.5secs the minimum waiting before retrying
        }
        logger.info("Flamingock will try to acquire the lock in {} mills", sleepingMillis);
        try {
            logger.info(
                    "Flamingock is going to sleep. Will retry in {}ms ({} minutes)",
                    sleepingMillis,
                    TimeUtil.millisToMinutes(sleepingMillis));
            Thread.sleep(sleepingMillis);
        } catch (InterruptedException ex) {
            logger.error("ERROR acquiring the lock", ex);
            Thread.currentThread().interrupt();
        }
    }


    @Override
    public String toString() {
        return "MongockLock{" +
                "owner='" + owner + '\'' +
                ", leaseMillis=" + leaseMillis +
                ", retryFrequencyMillis=" + retryFrequencyMillis +
                ", stopTryingAfterMillis=" + stopTryingAfterMillis +
                '}';
    }
}