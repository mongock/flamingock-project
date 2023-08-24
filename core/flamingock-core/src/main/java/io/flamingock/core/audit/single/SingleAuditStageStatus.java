package io.flamingock.core.audit.single;

import io.flamingock.core.audit.domain.AuditEntry;
import io.flamingock.core.audit.domain.AuditEntryStatus;
import io.flamingock.core.audit.domain.AuditStageStatus;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class SingleAuditStageStatus implements AuditStageStatus {

    public static Builder builder() {
        return new Builder();
    }

    private final Map<String, AuditEntryStatus> statesMap;

    private SingleAuditStageStatus(Map<String, AuditEntryStatus> statesMap) {
        this.statesMap = statesMap;
    }

    @Override
    public Optional<AuditEntryStatus> getEntryStatus(String taskId) {
        return Optional.ofNullable(statesMap.get(taskId));
    }

    @Override
    public Map<String, AuditEntryStatus> getStatesMap() {
        return statesMap;
    }


    public static class Builder {

        private final Map<String, AuditEntry> entryMap = new HashMap<>();

        public Builder addEntry(AuditEntry newEntry) {
            entryMap.compute(
                    newEntry.getChangeId(),
                    (changeId, currentEntry) -> AuditEntry.getMostRelevant(currentEntry, newEntry)
            );
            return this;
        }


        public SingleAuditStageStatus build() {
            Map<String, AuditEntryStatus> statesMap = entryMap.values().stream()
                    .collect(Collectors.toMap(AuditEntry::getChangeId, AuditEntry::getState));
            return new SingleAuditStageStatus(statesMap);
        }


    }
}
