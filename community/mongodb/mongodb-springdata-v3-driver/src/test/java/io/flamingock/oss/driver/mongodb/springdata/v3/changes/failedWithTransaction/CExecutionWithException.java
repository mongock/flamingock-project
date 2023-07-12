package io.flamingock.oss.driver.mongodb.springdata.v3.changes.failedWithTransaction;

import org.springframework.data.mongodb.core.MongoTemplate;

import io.flamingock.core.api.annotations.ChangeUnit;
import io.flamingock.core.api.annotations.Execution;
import io.flamingock.core.api.annotations.RollbackExecution;

@ChangeUnit( id="execution-with-exception" , order = "3")
public class CExecutionWithException {

    @Execution
    public void execution(MongoTemplate mongoTemplate) {
        throw new RuntimeException("test");
    }

    @RollbackExecution
    public void rollbackExecution(MongoTemplate mongoTemplate) {
        // Do nothing
    }
}
