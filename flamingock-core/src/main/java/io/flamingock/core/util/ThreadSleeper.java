package io.flamingock.core.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Function;

public class ThreadSleeper {

    private static final Logger logger = LoggerFactory.getLogger(ThreadSleeper.class);


    private final long totalMaxTimeWaitingMillis;
    private final long tryFrequencyMillis;
    private final StopWatch stopWatch;
    private final Function<String, RuntimeException> exceptionThrower;

    public ThreadSleeper(long totalMaxTimeWaitingMillis,
                         long tryFrequencyMillis,
                         StopWatch stopWatch,
                         Function<String, RuntimeException> exceptionThrower) {
        this.totalMaxTimeWaitingMillis = totalMaxTimeWaitingMillis;
        this.tryFrequencyMillis = tryFrequencyMillis;
        this.stopWatch = stopWatch;
        this.exceptionThrower = exceptionThrower;
    }

    public void checkThresholdAndWait() {
        //remaining time
        checkThresholdAndWait(totalMaxTimeWaitingMillis - stopWatch.getElapsed());
    }

    /**
     * It checks if the threshold hasn't been reached. In that case it will decide if it waits the maximum allowed
     * (maxTimeAllowedToWait) or less, which it's restricted by totalMaxTimeWaitingMillis
     * @param maxTimeAllowedToWait Max time allowed to wait in this iteration.
     */
    public void checkThresholdAndWait(long maxTimeAllowedToWait) {
        if (maxTimeAllowedToWait > 0) {
            checkThreshold();
            waitForMillis(maxTimeAllowedToWait);
        }

    }

    private void checkThreshold() {
        if (stopWatch.hasReached(totalMaxTimeWaitingMillis)) {
            throwException("Maximum waiting millis reached: " + totalMaxTimeWaitingMillis);
        }
    }

    private void waitForMillis(long maxAllowedTimeToWait) {
        try {
            long timeToSleep = maxAllowedTimeToWait;

            //How log until max Time waiting reached
            long remainingTime = totalMaxTimeWaitingMillis - stopWatch.getLap();
            if (remainingTime <= 0) {
                throwException("Maximum waiting millis reached: " + totalMaxTimeWaitingMillis);
            }

            if (timeToSleep > remainingTime) {
                timeToSleep = remainingTime;
            }

            if (timeToSleep > tryFrequencyMillis) {
                timeToSleep = tryFrequencyMillis;
            }

            Thread.sleep(timeToSleep);
        } catch (InterruptedException ex) {
            logger.error(ex.getMessage(), ex);
            Thread.currentThread().interrupt();
        }
    }

    private void throwException(String cause) {
        stopWatch.lap();
        throw exceptionThrower.apply(String.format(
                "Quit trying to acquire the lock after %d millis[ %s ]",
                stopWatch.getLap(),
                cause));
    }
}
