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

package io.flamingock.core.cloud.lock;

import io.flamingock.commons.utils.RunnerId;
import io.flamingock.commons.utils.TimeService;
import io.flamingock.core.engine.lock.*;
import io.flamingock.core.local.lock.LocalLockService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;

public class CloudLock extends Lock {

    private CloudLock(RunnerId owner,
                      LockKey lockKey,
                      long leaseMillis,
                      long stopTryingAfterMillis,
                      long retryFrequencyMillis,
                      LockService lockService,
                      TimeService timeService) {
        super(owner, lockKey, leaseMillis, stopTryingAfterMillis, retryFrequencyMillis, lockService, timeService);
    }

    public static Lock getLock(RunnerId owner,
                               LockKey lockKey,
                               long leaseMillis,
                               long stopTryingAfterMillis,
                               long retryFrequencyMillis,
                               long acquiredForMillis,
                               LockService lockService,
                               TimeService timeService) {
        CloudLock lock = new CloudLock(owner, lockKey, leaseMillis, stopTryingAfterMillis, retryFrequencyMillis, lockService, timeService);
        lock.updateLease(acquiredForMillis);
        return lock;

    }

}