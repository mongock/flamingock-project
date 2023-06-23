package io.flamingock.oss.driver.mongodb.v3.changes.failedWithoutTransactionWithoutRollback;

import com.mongodb.client.MongoDatabase;
import io.flamingock.core.api.annotations.ChangeUnit;
import io.flamingock.core.api.annotations.Execution;

@ChangeUnit( id="execution-with-exception" , order = "3")
public class CExecutionWithException {

    @Execution
    public void execution(MongoDatabase mongoDatabase) {
        throw new RuntimeException("test");
    }
}
