package io.flamingock.oss.driver.couchbase.changes.failedWithRollback;

import com.couchbase.client.java.Collection;
import com.couchbase.client.java.json.JsonObject;
import io.flamingock.core.api.annotations.ChangeUnit;
import io.flamingock.core.api.annotations.Execution;
import io.flamingock.core.api.annotations.RollbackExecution;

@ChangeUnit( id="execution-with-exception" , order = "3")
public class CExecutionWithException {

    @Execution
    public void execution(Collection collection) {
        collection.insert("test-client-Jorge", JsonObject.create().put("name", "Jorge"));
        throw new RuntimeException("test");
    }

    @RollbackExecution
    public void rollbackExecution(Collection collection) {
        collection.remove("test-client-Jorge");
    }
}
