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

package io.flamingock.cloud.transaction.mongodb.sync.config;

import com.mongodb.ReadConcernLevel;
import com.mongodb.ReadPreference;
import com.mongodb.WriteConcern;
import io.flamingock.internal.common.core.context.ContextResolver;
import io.flamingock.internal.common.mongodb.MongoDBDriverConfiguration;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

import static com.mongodb.assertions.Assertions.isTrue;

public class MongoDBSync4Configuration extends MongoDBDriverConfiguration {
    private ReadConcernLevel readConcern = ReadConcernLevel.MAJORITY;
    private WriteConcernLevel writeConcern = WriteConcernLevel.MAJORITY_WITH_JOURNAL;
    private ReadPreferenceLevel readPreference = ReadPreferenceLevel.PRIMARY;

    public ReadConcernLevel getReadConcern() {
        return readConcern;
    }

    public void setReadConcern(ReadConcernLevel readConcern) {
        this.readConcern = readConcern;
    }

    public void setReadConcern(String readConcern) {
        this.readConcern = ReadConcernLevel.fromString(readConcern);
    }

    public WriteConcernLevel getWriteConcern() {
        return writeConcern;
    }

    public void setWriteConcern(WriteConcernLevel writeConcern) {
        this.writeConcern = writeConcern;
    }

    public ReadPreferenceLevel getReadPreference() {
        return readPreference;
    }

    public void setReadPreference(ReadPreferenceLevel readPreference) {
        this.readPreference = readPreference;
    }

    public WriteConcern getBuiltMongoDBWriteConcern() {
        WriteConcern wc;
        if (writeConcern.w instanceof String) {
            wc = new WriteConcern((String) writeConcern.w).withJournal(writeConcern.journal);
        } else {
            wc = new WriteConcern((int) writeConcern.w).withJournal(writeConcern.journal);
        }
        return writeConcern.getwTimeoutMs() == null
                ? wc
                : wc.withWTimeout(writeConcern.getwTimeoutMs().toMillis(), TimeUnit.MILLISECONDS);
    }

    public void mergeConfig(ContextResolver dependencyContext) {
        dependencyContext.getPropertyAs("mongodb.autoCreate", Boolean.class)
                .ifPresent(this::setAutoCreate);
        dependencyContext.getPropertyAs("mongodb.auditRepositoryName", String.class)
                .ifPresent(this::setAuditRepositoryName);
        dependencyContext.getPropertyAs("mongodb.lockRepositoryName", String.class)
                .ifPresent(this::setLockRepositoryName);

        dependencyContext.getPropertyAs("mongodb.readConcern", String.class)
                .ifPresent(this::setReadConcern);

        dependencyContext.getPropertyAs("mongodb.writeConcern.w", Object.class)
                .ifPresent(this.writeConcern::setW);
        dependencyContext.getPropertyAs("mongodb.writeConcern.journal", Boolean.class)
                .ifPresent(this.writeConcern::setJournal);
        dependencyContext.getPropertyAs("mongodb.writeConcern.wTimeout", Duration.class)
                .ifPresent(this.writeConcern::setwTimeoutMs);

        dependencyContext.getPropertyAs("mongodb.readPreference", ReadPreferenceLevel.class)
                .ifPresent(this::setReadPreference);
    }

    public enum ReadPreferenceLevel {
        PRIMARY(ReadPreference.primary()),
        PRIMARY_PREFERRED(ReadPreference.primaryPreferred()),
        SECONDARY(ReadPreference.secondary()),
        SECONDARY_PREFERRED(ReadPreference.secondaryPreferred()),
        NEAREST(ReadPreference.nearest());

        private final ReadPreference value;

        ReadPreferenceLevel(ReadPreference value) {
            this.value = value;
        }

        public ReadPreference getValue() {
            return value;
        }
    }

    public static class WriteConcernLevel {

        public static final WriteConcernLevel MAJORITY_WITH_JOURNAL = new WriteConcernLevel(
                WriteConcern.MAJORITY.getWString(),
                Duration.ofSeconds(1),
                true);
        private Object w;
        private Duration wTimeoutMs;
        private Boolean journal;

        public WriteConcernLevel(String w, Duration wTimeoutMs, Boolean journal) {
            this.w = w;
            this.wTimeoutMs = wTimeoutMs;
            this.journal = journal;
        }

        public WriteConcernLevel() {
        }

        public int getW() {
            isTrue("w is an Integer", w != null && w instanceof Integer);
            return (Integer) w;
        }

        public String getStringW() {
            isTrue("w is a String", w != null && w instanceof String);
            return (String) w;
        }

        public void setW(Object w) {
            this.w = w;
        }

        public Duration getwTimeoutMs() {
            return wTimeoutMs;
        }

        public void setwTimeoutMs(Duration wTimeoutMs) {
            this.wTimeoutMs = wTimeoutMs;
        }

        public Boolean isJournal() {
            return journal;
        }

        public void setJournal(Boolean journal) {
            this.journal = journal;
        }
    }
}
