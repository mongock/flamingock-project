package io.mongock.driver.mongodb.sync.v4.internal;

import com.mongodb.TransactionOptions;
import com.mongodb.client.ClientSession;
import io.mongock.core.mongodb.SessionWrapper;
import io.flamingock.oss.core.runtime.dependency.DependencyInjector;
import io.flamingock.oss.core.task.descriptor.TaskDescriptor;
import io.flamingock.oss.core.transaction.TransactionWrapper;
import io.flamingock.oss.core.util.Failed;
import io.mongock.driver.mongodb.sync.v4.internal.mongodb.MongoSync4SessionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Supplier;

public class MongoSync4TransactionWrapper implements TransactionWrapper {
    private static final Logger logger = LoggerFactory.getLogger(MongoSync4TransactionWrapper.class);

    private final MongoSync4SessionManager sessionManager;

    MongoSync4TransactionWrapper(MongoSync4SessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }

    @Override
    public <T> T wrapInTransaction(TaskDescriptor taskDescriptor, DependencyInjector dependencyInjector, Supplier<T> operation) {
        logger.info("--------------------------------------------------------------------STARTING TRANSACTION WRAPPER");
        try (SessionWrapper<ClientSession> sessionWrapper = sessionManager.startSession(taskDescriptor.getId())) {
            logger.info("--------------------------------------------------------------------SESSION ACQUIRED");
            ClientSession clientSession = sessionWrapper.getClientSession();
            clientSession.startTransaction(TransactionOptions.builder().build());
            dependencyInjector.addDependency(clientSession);
            T result = operation.get();
            if (result instanceof Failed) {
                logger.info("--------------------------------------------------------------------EXECUTION FAILED");
                clientSession.abortTransaction();
            } else {
                logger.info("--------------------------------------------------------------------EXECUTION OK");
                clientSession.commitTransaction();
            }
            return result;

        }
    }


}
