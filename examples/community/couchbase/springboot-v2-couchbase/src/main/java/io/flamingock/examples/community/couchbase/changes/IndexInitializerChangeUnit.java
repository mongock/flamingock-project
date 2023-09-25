package io.flamingock.examples.community.couchbase.changes;

import com.couchbase.client.java.Cluster;
import com.couchbase.client.java.manager.query.DropQueryIndexOptions;

import io.flamingock.core.api.annotations.BeforeExecution;
import io.flamingock.core.api.annotations.ChangeUnit;
import io.flamingock.core.api.annotations.Execution;
import io.flamingock.core.api.annotations.RollbackBeforeExecution;
import io.flamingock.core.api.annotations.RollbackExecution;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.couchbase.CouchbaseClientFactory;

import java.util.Arrays;

@ChangeUnit(id = "index-initializer", order = "1")
public class IndexInitializerChangeUnit {

	private static final Logger logger = LoggerFactory.getLogger(IndexInitializerChangeUnit.class);

	@BeforeExecution
	public void beforeExecution(CouchbaseClientFactory clientFactory) {
		logger.debug("beforeExecution with bucket {}", clientFactory.getBucket().name());
	}

	@RollbackBeforeExecution
	public void rollbackBeforeExecution(CouchbaseClientFactory clientFactory) {
		logger.debug("rollbackBeforeExecution with bucket {}", clientFactory.getBucket().name());
	}
	@Execution
	public void execution(Cluster cluster) {
		cluster.queryIndexes().createIndex("bucket", "idx_springboot_index", Arrays.asList("field1, field2"));
	}

	@RollbackExecution
	public void rollbackExecution(Cluster cluster) {
		cluster.queryIndexes().dropIndex("bucket", "idx_springboot_index", DropQueryIndexOptions.dropQueryIndexOptions().ignoreIfNotExists(true));
	}

}
