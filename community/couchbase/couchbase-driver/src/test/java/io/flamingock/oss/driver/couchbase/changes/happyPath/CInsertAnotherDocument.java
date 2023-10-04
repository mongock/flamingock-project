package io.flamingock.oss.driver.couchbase.changes.happyPath;

import com.couchbase.client.java.Collection;
import com.couchbase.client.java.json.JsonObject;
import io.flamingock.core.api.annotations.ChangeUnit;
import io.flamingock.core.api.annotations.Execution;

@ChangeUnit( id="insert-another-document" , order = "3")
public class CInsertAnotherDocument {

    @Execution
    public void execution(Collection collection) {
        collection.insert("test-client-Jorge", JsonObject.create().put("name", "Jorge"));
    }
}