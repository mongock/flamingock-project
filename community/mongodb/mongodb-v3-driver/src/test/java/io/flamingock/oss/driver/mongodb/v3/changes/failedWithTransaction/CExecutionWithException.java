package io.flamingock.oss.driver.mongodb.v3.changes.failedWithTransaction;

import com.mongodb.client.ClientSession;
import com.mongodb.client.MongoDatabase;
import io.flamingock.core.api.annotations.ChangeUnit;
import io.flamingock.core.api.annotations.Execution;
import io.flamingock.core.api.annotations.RollbackExecution;

@ChangeUnit( id="execution-with-exception" , order = "3")
public class CExecutionWithException {

    @Execution
    public void execution(MongoDatabase mongoDatabase, ClientSession clientSession) {
        throw new RuntimeException("test");
    }

    @RollbackExecution
    public void rollbackExecution(MongoDatabase mongoDatabase, ClientSession clientSession) {
        // Do nothing
    }
}
