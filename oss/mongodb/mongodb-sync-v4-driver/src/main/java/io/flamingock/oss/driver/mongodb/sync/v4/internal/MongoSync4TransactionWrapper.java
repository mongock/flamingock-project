package io.flamingock.oss.driver.mongodb.sync.v4.internal;

import com.mongodb.TransactionOptions;
import com.mongodb.client.ClientSession;
import io.flamingock.core.core.runtime.dependency.DependencyInjector;
import io.flamingock.core.core.task.descriptor.TaskDescriptor;
import io.flamingock.core.core.transaction.TransactionWrapper;
import io.flamingock.core.core.util.Failed;
import io.flamingock.oss.driver.common.mongodb.MongoSync4SessionManagerGeneric;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Supplier;

public class MongoSync4TransactionWrapper implements TransactionWrapper {
    private static final Logger logger = LoggerFactory.getLogger(MongoSync4TransactionWrapper.class);

    private final MongoSync4SessionManagerGeneric<ClientSession> sessionManager;

    MongoSync4TransactionWrapper(MongoSync4SessionManagerGeneric<ClientSession> sessionManager) {
        this.sessionManager = sessionManager;
    }

    @Override
    public <T> T wrapInTransaction(TaskDescriptor taskDescriptor, DependencyInjector dependencyInjector, Supplier<T> operation) {
        String sessionId = taskDescriptor.getId();
        try (ClientSession clientSession = sessionManager.startSession(sessionId)) {
            clientSession.startTransaction(TransactionOptions.builder().build());
            dependencyInjector.addDependency(clientSession);
            T result = operation.get();
            if (result instanceof Failed) {
                clientSession.abortTransaction();
            } else {
                clientSession.commitTransaction();
            }
            return result;
        } finally {
            //Although the ClientSession itself has been closed, it needs to be removed from the map
            sessionManager.closeSession(sessionId);
        }
    }


}
