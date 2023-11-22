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

package io.flamingock.core.engine.lock;

import java.net.Inet4Address;
import java.net.UnknownHostException;
import java.util.UUID;


public class LockOptions {

    public static Builder builder() {
        return new Builder();
    }

    private final boolean withDaemon;
    private final String owner;

    private LockOptions(boolean withDaemon, String owner) {
        this.withDaemon = withDaemon;
        this.owner = owner;
    }

    public boolean isWithDaemon() {
        return withDaemon;
    }

    public String getOwner() {
        return owner;
    }


    public static class Builder {
        private boolean withDaemon = true;
        private String owner = null;

        public Builder withDaemon(boolean withDaemon) {
            this.withDaemon = withDaemon;
            return this;
        }

        public Builder setOwner(String owner) {
            this.owner = owner;
            return this;
        }

        public LockOptions build() {
            return new LockOptions(withDaemon, owner != null ? owner : generateDefaultOwner());
        }


        private static String generateDefaultOwner() {
            try {
                return Inet4Address.getLocalHost().getHostName() + UUID.randomUUID();
            } catch (final UnknownHostException e) {
                return UUID.randomUUID().toString();
            }
        }
    }
}
