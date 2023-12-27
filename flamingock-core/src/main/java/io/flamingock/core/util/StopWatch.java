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

package io.flamingock.core.util;

public class StopWatch {

    private long startedAt = -1L;

    private long lap = -1L;

    public static StopWatch getNoStarted() {
        return new StopWatch();
    }

    public static StopWatch startAndGet() {
        StopWatch stopWatch = new StopWatch();
        stopWatch.run();
        return stopWatch;
    }

    private StopWatch() {
    }

    /**
     * Idempotent operation. If it's already started, it doesn't have effect
     */
    public void run() {
        if (isNotStarted()) {
            startedAt = System.currentTimeMillis();
        }
    }

    /**
     * Performs a snapshot of the current elapse
     */
    public void lap() {
        lap = getElapsed();
    }

    /**
     * @return The latest lap, if the stopwatch has been lapped. 0L otherwise
     */
    public long getLap() {
        return Math.max(lap, 0L);
    }

    /**
     * @return the current stopwatch's elapse
     */
    public long getElapsed() {
        if (isNotStarted()) {
            return 0L;
        }
        return System.currentTimeMillis() - startedAt;
    }

    public boolean hasReached(long limitMillis) {
        long elapsed = System.currentTimeMillis() - startedAt;
        return elapsed >= limitMillis;
    }

    public void resetLap() {
        lap = -1L;
    }

    public boolean isNotStarted() {
        return startedAt <= -1L;
    }

}
