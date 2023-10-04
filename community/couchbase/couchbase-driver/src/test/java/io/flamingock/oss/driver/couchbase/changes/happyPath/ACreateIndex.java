package io.flamingock.oss.driver.couchbase.changes.happyPath;

import com.couchbase.client.java.Cluster;
import io.flamingock.core.api.annotations.ChangeUnit;
import io.flamingock.core.api.annotations.Execution;

import java.util.Arrays;

@ChangeUnit(id = "create-index", order = "1")
public class ACreateIndex {

	@Execution
	public void execution(Cluster cluster) {
		cluster.queryIndexes().createIndex("bucket", "idx_standalone_index", Arrays.asList("field1, field2"));
	}
}
