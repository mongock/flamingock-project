package io.flamingock.oss.driver.couchbase.changes.failedWithoutRollback;

import com.couchbase.client.java.Collection;
import com.couchbase.client.java.json.JsonObject;
import io.flamingock.core.api.annotations.ChangeUnit;
import io.flamingock.core.api.annotations.Execution;

@ChangeUnit( id="execution-with-exception" , order = "3")
public class CExecutionWithException {

    @Execution
    public void execution(Collection collection) {
        collection.insert("test-client-Jorge", JsonObject.create().put("name", "Jorge"));
        throw new RuntimeException("test");
    }
}
