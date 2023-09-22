package io.flamingock.examples.community.couchbase.changes;

import com.couchbase.client.java.Cluster;
import com.couchbase.client.java.Collection;
import com.couchbase.client.java.manager.query.DropQueryIndexOptions;

import io.flamingock.core.api.annotations.BeforeExecution;
import io.flamingock.core.api.annotations.ChangeUnit;
import io.flamingock.core.api.annotations.Execution;
import io.flamingock.core.api.annotations.RollbackBeforeExecution;
import io.flamingock.core.api.annotations.RollbackExecution;
import io.flamingock.examples.community.couchbase.ChangesTracker;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;

@ChangeUnit(id = "index-initializer", order = "1")
public class IndexInitializerChangeUnit {

	private static final Logger logger = LoggerFactory.getLogger(IndexInitializerChangeUnit.class);

	@BeforeExecution
	public void beforeExecution(Collection collection) {
		ChangesTracker.changes.add(getClass().getName() + "_beforeExecution");
		logger.debug("beforeExecution with bucket {}", collection.bucketName());
	}

	@RollbackBeforeExecution
	public void rollbackBeforeExecution(Collection collection) {
		ChangesTracker.changes.add(getClass().getName() + "_rollbackBeforeExecution");
		logger.debug("rollbackBeforeExecution with bucket {}", collection.bucketName());
	}
	@Execution
	public void execution(Cluster cluster) {
		ChangesTracker.changes.add(getClass().getName() + "_execution");
		cluster.queryIndexes().createIndex("bucket", "idx_standalone_index", Arrays.asList("field1, field2"));
	}

	@RollbackExecution
	public void rollbackExecution(Cluster cluster) {
		ChangesTracker.changes.add(getClass().getName() + "_rollbackExecution");
		cluster.queryIndexes().dropIndex("bucket", "idx_standalone_index", DropQueryIndexOptions.dropQueryIndexOptions().ignoreIfNotExists(true));
	}

}
