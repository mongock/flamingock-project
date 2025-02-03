package io.flamingock.importer.cloud.dynamodb;

import io.flamingock.commons.utils.id.EnvironmentId;
import io.flamingock.commons.utils.id.ServiceId;
import io.flamingock.importer.cloud.common.MongockLegacyAuditEntry;
import io.flamingock.importer.cloud.common.MongockLegacyImporterConfiguration;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.model.ScanEnhancedRequest;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class DynamoDBLegacyImportConfiguration implements MongockLegacyImporterConfiguration {

    private final DynamoDbTable<ChangeEntry> changeUnitsStorage;
    private final EnvironmentId environmentId;
    private final String jwt;
    private final ServiceId serviceId;
    private final String serverHost;

    public DynamoDBLegacyImportConfiguration(EnvironmentId environmentId,
                                             ServiceId serviceId,
                                             String jwt,
                                             String serverHost,
                                             DynamoDbTable<ChangeEntry> changeUnitsStorage) {
        this.environmentId = environmentId;
        this.serviceId = serviceId;
        this.jwt = jwt;
        this.serverHost = serverHost;
        this.changeUnitsStorage = changeUnitsStorage;
    }

    static MongockLegacyAuditEntry toMongockLegacyAuditEntry(ChangeEntry changeEntryDynamoDB) {
        return new MongockLegacyAuditEntry(
                changeEntryDynamoDB.getExecutionId(),
                changeEntryDynamoDB.getChangeId(),
                changeEntryDynamoDB.getState(),
                changeEntryDynamoDB.getType(),
                changeEntryDynamoDB.getAuthor(),
                Date.from(Instant.ofEpochMilli(changeEntryDynamoDB.getTimestamp())).getTime(),
                changeEntryDynamoDB.getChangeLogClass(),
                changeEntryDynamoDB.getChangeSetMethod(),
                changeEntryDynamoDB.getMetadata(),
                changeEntryDynamoDB.getExecutionMillis(),
                changeEntryDynamoDB.getExecutionHostname(),
                changeEntryDynamoDB.getErrorTrace(),
                changeEntryDynamoDB.getSystemChange()
        );
    }

    @Override
    public EnvironmentId getEnvironmentId() {
        return environmentId;
    }

    @Override
    public ServiceId getServiceId() {
        return serviceId;
    }

    @Override
    public String getJwt() {
        return jwt;
    }

    @Override
    public String getServerHost() {
        return serverHost;
    }

    @Override
    public List<MongockLegacyAuditEntry> readMongockLegacyAuditEntries() {
        return changeUnitsStorage
                .scan(ScanEnhancedRequest.builder()
                        .consistentRead(true)
                        .build()
                )
                .items()
                .stream()
                .map(DynamoDBLegacyImportConfiguration::toMongockLegacyAuditEntry)
                .sorted(Comparator.comparing(MongockLegacyAuditEntry::getTimestamp))
                .collect(Collectors.toList());
    }
}
