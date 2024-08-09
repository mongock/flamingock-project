package io.flamingock.core;

import io.flamingock.core.cloud.api.audit.AuditEntryRequest;
import io.flamingock.core.cloud.api.transaction.OngoingStatus;
import io.flamingock.core.engine.audit.domain.AuditItem;
import io.flamingock.core.engine.audit.writer.AuditEntry;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class ConversionsTest {

    @Test
    @DisplayName("AuditItem.Operation should match OnGoingStatus.Operation")
    void auditItemOperationShouldMatchOngoingStatusOperationValues() {
        Set<String> onGoingStatusValues = Arrays
                .stream(OngoingStatus.Operation.values())
                .map(OngoingStatus.Operation::name)
                .collect(Collectors.toSet());
        List<String> auditItemOperationValues = Arrays.stream(AuditItem.Operation.values())
                .map(AuditItem.Operation::name)
                .collect(Collectors.toList());

        if (onGoingStatusValues.size() != auditItemOperationValues.size()) {
            throw new IllegalArgumentException("Enums OngoingStatus.Operation and AuditItem.Operation should match");
        }

        if(auditItemOperationValues.stream().anyMatch(value -> !onGoingStatusValues.contains(value))) {
            throw new IllegalArgumentException("Enums OngoingStatus.Operation and AuditItem.Operation should match");

        }
    }

    @Test
    @DisplayName("AuditEntryRequest.ExecutionType should match AuditEntry.ExecutionType")
    void auditEntryExecutionTypeShouldAuditEntryRequestExecutionType() {
        Set<String> auditEntryRequestExecutionTypeValues = Arrays
                .stream(AuditEntryRequest.ExecutionType.values())
                .map(AuditEntryRequest.ExecutionType::name)
                .collect(Collectors.toSet());
        List<String> auditEntryExecutionTypeValues = Arrays.stream(AuditEntry.ExecutionType.values())
                .map(AuditEntry.ExecutionType::name)
                .collect(Collectors.toList());

        if (auditEntryExecutionTypeValues.size() != auditEntryRequestExecutionTypeValues.size()) {
            throw new IllegalArgumentException("Enums AuditEntryRequest.ExecutionType and AuditEntry.ExecutionType should match");
        }

        if(auditEntryExecutionTypeValues.stream().anyMatch(value -> !auditEntryRequestExecutionTypeValues.contains(value))) {
            throw new IllegalArgumentException("Enums AuditEntryRequest.ExecutionType and AuditEntry.ExecutionType should match");
        }
    }


    @Test
    @DisplayName("AuditEntryRequest.Status should match AuditEntry.Status")
    void auditEntryStateShouldAuditEntryRequestState() {
        Set<String> auditEntryRequestExecutionTypeValues = Arrays
                .stream(AuditEntryRequest.Status.values())
                .map(AuditEntryRequest.Status::name)
                .collect(Collectors.toSet());
        List<String> auditEntryExecutionTypeValues = Arrays.stream(AuditEntry.Status.values())
                .map(AuditEntry.Status::name)
                .collect(Collectors.toList());

        if (auditEntryExecutionTypeValues.size() != auditEntryRequestExecutionTypeValues.size()) {
            throw new IllegalArgumentException("Enums AuditEntryRequest.Status and AuditEntry.Status should match");
        }

        if(auditEntryExecutionTypeValues.stream().anyMatch(value -> !auditEntryRequestExecutionTypeValues.contains(value))) {
            throw new IllegalArgumentException("Enums AuditEntryRequest.Status and AuditEntry.Status should match");
        }
    }
}