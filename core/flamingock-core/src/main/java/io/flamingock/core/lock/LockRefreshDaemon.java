package io.flamingock.core.lock;

import io.flamingock.core.util.TimeService;
import io.flamingock.core.util.TimeUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LockRefreshDaemon extends Thread {

    private static final Logger logger = LoggerFactory.getLogger(LockRefreshDaemon.class);

    private final Lock lock;

    private final TimeService timeService;

    public LockRefreshDaemon(Lock lock, TimeService timeService) {
        this.lock = lock;
        this.timeService = timeService;
        setDaemon(true);
    }

    @Override
    public void run() {
        logger.info("Starting Flamingock lock daemon...");
        boolean keepRefreshing = true;
        do {
            try {
                logger.debug("Flamingock(daemon) refreshing lock");
                keepRefreshing = lock.refresh();
            } catch (LockException e) {
                logger.warn("Flamingock(daemon)Error refreshing lock: {}", e.getMessage());
            } catch (Exception e) {
                logger.warn("Flamingock(daemon)Generic error from daemon: {}", e.getMessage());
            }
            reposeIfRequired();
        } while (keepRefreshing);
        logger.info("Cancelled Flamingock lock daemon");
    }

    private void reposeIfRequired() {


        if (!lock.isExpired()) {
            try {
                long sleepingTime = TimeUtil.diffInMillis(lock.expiresAt(), timeService.currentDateTime()) / 3;
                logger.info("Lock acquired until[{}]. Lock daemon sleeping until[{}]...for {}ms",
                        lock.expiresAt(),
                        timeService.currentDatePlusMillis(sleepingTime),
                        sleepingTime);
                sleep(sleepingTime);

            } catch (InterruptedException ex) {
                logger.warn("Interrupted exception ignored");
            }
        } else {
            logger.debug("Lock is canceled/expired[{}]", lock.expiresAt());
        }

    }


}