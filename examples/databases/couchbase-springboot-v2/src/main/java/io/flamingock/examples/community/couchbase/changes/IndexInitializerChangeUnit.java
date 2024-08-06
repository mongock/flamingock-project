/*
 * Copyright 2023 Flamingock (https://oss.flamingock.io)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.flamingock.examples.community.couchbase.changes;

import com.couchbase.client.java.Cluster;
import com.couchbase.client.java.manager.query.DropQueryIndexOptions;

import flamingock.core.api.annotations.BeforeExecution;
import flamingock.core.api.annotations.ChangeUnit;
import flamingock.core.api.annotations.Execution;
import flamingock.core.api.annotations.RollbackBeforeExecution;
import flamingock.core.api.annotations.RollbackExecution;

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
