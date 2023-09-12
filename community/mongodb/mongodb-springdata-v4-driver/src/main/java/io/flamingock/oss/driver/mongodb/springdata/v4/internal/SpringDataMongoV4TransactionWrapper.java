package io.flamingock.oss.driver.mongodb.springdata.v4.internal;

import com.mongodb.TransactionOptions;

import io.flamingock.core.task.navigation.step.FailedStep;
import io.flamingock.core.runtime.dependency.DependencyInjectable;
import io.flamingock.core.task.descriptor.TaskDescriptor;
import io.flamingock.core.transaction.TransactionWrapper;
import io.flamingock.oss.driver.mongodb.sync.v4.internal.mongodb.ReadWriteConfiguration;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.MongoTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import java.util.function.Supplier;

public class SpringDataMongoV4TransactionWrapper implements TransactionWrapper {

    private final MongoTransactionManager txManager;

    SpringDataMongoV4TransactionWrapper(MongoTemplate mongoTemplate, ReadWriteConfiguration readWriteConfiguration) {
        this.txManager = new MongoTransactionManager(mongoTemplate.getMongoDatabaseFactory(), 
                                                    TransactionOptions.builder()
                                                                        .readConcern(readWriteConfiguration.getReadConcern())
                                                                        .readPreference(readWriteConfiguration.getReadPreference())
                                                                        .writeConcern(readWriteConfiguration.getWriteConcern())
                                                                        .build());
    }

    @Override
    public <T> T wrapInTransaction(TaskDescriptor taskDescriptor, DependencyInjectable dependencyInjectable, Supplier<T> operation) {
        TransactionStatus txStatus = getTxStatus(txManager);
        T result = operation.get();
        if (result instanceof FailedStep) {
            txManager.rollback(txStatus);
        } else {
            txManager.commit(txStatus);
        }
        return result;
    }

    protected TransactionStatus getTxStatus(PlatformTransactionManager txManager) {
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        // explicitly setting the transaction name is something that can be done only
        // programmatically
        def.setName("flamingock-transaction-spring-data-3");
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        return txManager.getTransaction(def);
    }
}