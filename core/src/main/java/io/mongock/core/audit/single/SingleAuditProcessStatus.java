package io.mongock.core.audit.single;

import io.mongock.core.audit.domain.AuditEntry;
import io.mongock.core.audit.domain.AuditEntryStatus;
import io.mongock.core.audit.domain.AuditProcessStatus;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static io.mongock.core.audit.domain.AuditEntryStatus.EXECUTED;
import static io.mongock.core.audit.domain.AuditEntryStatus.FAILED;
import static io.mongock.core.audit.domain.AuditEntryStatus.ROLLBACK_FAILED;
import static io.mongock.core.audit.domain.AuditEntryStatus.ROLLED_BACK;

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
        private static final Set<AuditEntryStatus> RELEVANT_STATES = Collections.unmodifiableSet(new HashSet<>(Arrays.asList(
                EXECUTED,
                ROLLED_BACK,
                FAILED,
                ROLLBACK_FAILED)));

        private final Map<String, AuditEntry> entryMap = new HashMap<>();

        public Builder addEntry(AuditEntry newEntry) {
            if (!entryMap.containsKey(newEntry.getChangeId())) {
                entryMap.put(newEntry.getChangeId(), newEntry);
                return this;
            }
            AuditEntry currentEntry = entryMap.get(newEntry.getChangeId());
            if (RELEVANT_STATES.contains(newEntry.getState()) && newEntry.getCreatedAt().isAfter(currentEntry.getCreatedAt())) {
                entryMap.put(newEntry.getChangeId(), newEntry);
            }
            return this;
        }


        public SingleAuditProcessStatus build() {
            Map<String, AuditEntryStatus> statesMap = entryMap.values().stream()
                    .collect(Collectors.toMap(AuditEntry::getChangeId, AuditEntry::getState));
            return new SingleAuditProcessStatus(statesMap);
        }

    }
}
