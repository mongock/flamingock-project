package io.flamingock.oss.driver.couchbase.changes.happyPath;

import com.couchbase.client.java.Collection;
import com.couchbase.client.java.json.JsonObject;
import io.flamingock.core.api.annotations.ChangeUnit;
import io.flamingock.core.api.annotations.Execution;

@ChangeUnit( id="insert-document" , order = "2")
public class BInsertDocument {

    @Execution
    public void execution(Collection collection) {
        collection.insert("test-client-Federico", JsonObject.create().put("name", "Federico"));
    }
}
