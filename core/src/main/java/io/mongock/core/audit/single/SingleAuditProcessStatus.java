package io.mongock.core.audit.single;

import io.mongock.core.audit.domain.AuditEntryStatus;
import io.mongock.core.audit.domain.AuditProcessStatus;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class SingleAuditProcessStatus implements AuditProcessStatus {

    private final Map<String, AuditEntryStatus> statesMap = new HashMap<>();

    @Override
    public Optional<AuditEntryStatus> getEntryStatus(String taskId) {
        return Optional.ofNullable(statesMap.get(taskId));
    }
}
