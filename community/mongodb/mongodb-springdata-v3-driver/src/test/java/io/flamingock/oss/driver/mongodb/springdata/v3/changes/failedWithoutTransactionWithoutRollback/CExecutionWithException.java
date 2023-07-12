package io.flamingock.oss.driver.mongodb.springdata.v3.changes.failedWithoutTransactionWithoutRollback;

import org.springframework.data.mongodb.core.MongoTemplate;

import io.flamingock.core.api.annotations.ChangeUnit;
import io.flamingock.core.api.annotations.Execution;

@ChangeUnit( id="execution-with-exception" , order = "3")
public class CExecutionWithException {

    @Execution
    public void execution(MongoTemplate mongoTemplate) {
        throw new RuntimeException("test");
    }
}
