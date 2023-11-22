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

package io.flamingock.core.driver.audit.writer;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class AuditStageStatus {

    public static Builder builder() {
        return new Builder();
    }

    private final Map<String, AuditEntryStatus> entryStatesMap;

    private AuditStageStatus(Map<String, AuditEntryStatus> entryStatesMap) {
        this.entryStatesMap = entryStatesMap;
    }

    public Map<String, AuditEntryStatus> getEntryStatesMap() {
        return entryStatesMap;
    }

    public static class Builder {

        private final Map<String, AuditEntry> entryMap = new HashMap<>();

        public void addEntry(AuditEntry newEntry) {
            entryMap.compute(
                    newEntry.getChangeId(),
                    (changeId, currentEntry) -> AuditEntry.getMostRelevant(currentEntry, newEntry)
            );
        }

        public AuditStageStatus build() {
            Map<String, AuditEntryStatus> statesMap = entryMap.values().stream()
                    .collect(Collectors.toMap(AuditEntry::getChangeId, AuditEntry::getState));
            return new AuditStageStatus(statesMap);
        }

    }
}