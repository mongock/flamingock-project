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

import java.util.Optional;

public abstract class LockAcquisition implements AutoCloseable {

    private final boolean required;

    public LockAcquisition(boolean required) {
        this.required = required;
    }

    public final boolean isNotRequired() {
        return !required;
    }

    public abstract Optional<Lock> lock();

    public static class Acquired extends LockAcquisition {
        private final Lock lock;


        public Acquired(Lock lock) {
            super(true);
            this.lock = lock;
        }

        public Optional<Lock> lock() {
            return Optional.of(lock);
        }


        @Override
        public void close() {
            lock.release();
        }


    }

    public static class NoRequired extends LockAcquisition {

        public NoRequired() {
            super(false);
        }

        public Optional<Lock> lock() {
            return Optional.empty();
        }

        @Override
        public void close() {

        }
    }
}
