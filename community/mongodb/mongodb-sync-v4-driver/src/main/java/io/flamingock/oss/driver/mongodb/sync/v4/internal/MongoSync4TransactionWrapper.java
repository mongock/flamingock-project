package io.flamingock.oss.driver.mongodb.sync.v4.internal;

import com.mongodb.TransactionOptions;
import com.mongodb.client.ClientSession;
import io.flamingock.core.core.runtime.dependency.DependencyInjectable;
import io.flamingock.core.core.task.descriptor.TaskDescriptor;
import io.flamingock.core.core.transaction.TransactionWrapper;
import io.flamingock.core.core.execution.step.FailedStep;
import io.flamingock.oss.driver.common.mongodb.SessionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Supplier;

public class MongoSync4TransactionWrapper implements TransactionWrapper {
    private static final Logger logger = LoggerFactory.getLogger(MongoSync4TransactionWrapper.class);

    private final SessionManager<ClientSession> sessionManager;

    MongoSync4TransactionWrapper(SessionManager<ClientSession> sessionManager) {
        this.sessionManager = sessionManager;
    }

    @Override
    public <T> T wrapInTransaction(TaskDescriptor taskDescriptor, DependencyInjectable dependencyInjectable, Supplier<T> operation) {
        String sessionId = taskDescriptor.getId();
        try (ClientSession clientSession = sessionManager.startSession(sessionId)) {
            clientSession.startTransaction(TransactionOptions.builder().build());
            dependencyInjectable.addDependency(clientSession);
            T result = operation.get();
            if (result instanceof FailedStep) {
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
