package io.flamingock.oss.driver.couchbase;

import com.couchbase.client.java.Cluster;
import com.couchbase.client.java.Collection;
import com.couchbase.client.java.json.JsonObject;
import com.couchbase.client.java.query.QueryOptions;
import com.couchbase.client.java.query.QueryResult;
import com.couchbase.client.java.query.QueryScanConsistency;
import io.flamingock.core.audit.domain.AuditEntry;
import io.flamingock.oss.driver.couchbase.internal.util.CouchBaseUtil;
import io.flamingock.oss.driver.couchbase.internal.util.N1QLQueryProvider;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static io.flamingock.oss.driver.couchbase.internal.CouchbaseConstants.DOCUMENT_TYPE_AUDIT_ENTRY;

public class CouchbaseTestHelper {

    private final Cluster cluster;

    public CouchbaseTestHelper(Cluster cluster) {
        this.cluster = cluster;
    }

    public List<AuditEntry> getAuditEntriesSorted(Collection collection) {
        QueryResult result = cluster.query(
                N1QLQueryProvider.selectAllChangesQuery(collection.bucketName(), collection.scopeName(),
                        collection.name()),
                QueryOptions.queryOptions().parameters(JsonObject.create().put("p", DOCUMENT_TYPE_AUDIT_ENTRY))
                        .scanConsistency(QueryScanConsistency.REQUEST_PLUS));
        return result
                .rowsAsObject()
                .stream()
                .map(CouchBaseUtil::auditEntryFromEntity)
                .sorted(Comparator.comparing(AuditEntry::getCreatedAt))
                .collect(Collectors.toList());
    }

    public boolean indexExists(String bucketName, String indexName) {
        return cluster
                .queryIndexes()
                .getAllIndexes(bucketName)
                .stream()
                .filter(i -> i.name().equals(indexName))
                .count() == 1;
    }
}
