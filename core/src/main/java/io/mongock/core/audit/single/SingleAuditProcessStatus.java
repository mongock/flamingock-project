package io.mongock.core.audit.single;

import io.mongock.core.audit.domain.AuditEntry;
import io.mongock.core.audit.domain.AuditEntryStatus;
import io.mongock.core.audit.domain.AuditProcessStatus;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class SingleAuditProcessStatus implements AuditProcessStatus {

    public static Builder builder() {
        return new Builder();
    }

    private final Map<String, AuditEntryStatus> statesMap;

    private SingleAuditProcessStatus(Map<String, AuditEntryStatus> statesMap) {
        this.statesMap = statesMap;
    }

    @Override
    public Optional<AuditEntryStatus> getEntryStatus(String taskId) {
        return Optional.ofNullable(statesMap.get(taskId));
    }

    public static class Builder {

        private final Map<String, AuditEntry> entryMap = new HashMap<>();

        public Builder addEntry(AuditEntry newEntry) {
            entryMap.compute(
                    newEntry.getChangeId(),
                    (changeId, currentEntry) -> getMostRelevant(currentEntry, newEntry)
            );
            return this;
        }


        public SingleAuditProcessStatus build() {
            Map<String, AuditEntryStatus> statesMap = entryMap.values().stream()
                    .collect(Collectors.toMap(AuditEntry::getChangeId, AuditEntry::getState));
            return new SingleAuditProcessStatus(statesMap);
        }

        private static AuditEntry getMostRelevant(AuditEntry currentEntry, AuditEntry newEntry) {
            if (currentEntry != null) {
                return currentEntry.shouldBeReplacedBy(newEntry) ? newEntry : currentEntry;
            } else {
                return newEntry;
            }
        }

    }
}
