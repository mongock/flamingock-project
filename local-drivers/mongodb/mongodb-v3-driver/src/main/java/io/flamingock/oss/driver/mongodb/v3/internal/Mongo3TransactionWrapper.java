package io.flamingock.oss.driver.mongodb.v3.internal;

import com.mongodb.TransactionOptions;
import com.mongodb.client.ClientSession;
import io.flamingock.core.task.navigation.step.FailedStep;
import io.flamingock.core.runtime.dependency.DependencyInjectable;
import io.flamingock.core.task.descriptor.TaskDescriptor;
import io.flamingock.core.transaction.TransactionWrapper;
import io.flamingock.oss.driver.common.mongodb.SessionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Supplier;

public class Mongo3TransactionWrapper implements TransactionWrapper {
    private static final Logger logger = LoggerFactory.getLogger(Mongo3TransactionWrapper.class);

    private final SessionManager<ClientSession> sessionManager;

    Mongo3TransactionWrapper(SessionManager<ClientSession> sessionManager) {
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
