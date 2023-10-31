package io.flamingock.core.audit.domain;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
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
