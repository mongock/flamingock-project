package io.mongock.driver.mongodb.sync.v4.internal;

import com.mongodb.TransactionOptions;
import com.mongodb.client.ClientSession;
import com.mongodb.client.TransactionBody;
import io.mongock.core.execution.navigator.StepNavigator;
import io.mongock.core.mongodb.SessionWrapper;
import io.mongock.core.task.descriptor.TaskDescriptor;
import io.mongock.core.transaction.TransactionWrapper;
import io.mongock.core.util.Result;
import io.mongock.driver.mongodb.sync.v4.internal.mongodb.MongoSync4SessionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Supplier;

public class MongoSync4TransactionWrapper implements TransactionWrapper {
    private static final Logger logger = LoggerFactory.getLogger(StepNavigator.class);

    private final MongoSync4SessionManager sessionManager;

    MongoSync4TransactionWrapper(MongoSync4SessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }

    @Override
    public Result wrapInTransaction(TaskDescriptor taskDescriptor, Runnable operation) {
        logger.info("--------------------------------------------------------------------STARTING TRANSACTION WRAPPER");
        try (SessionWrapper<ClientSession> clientSession = sessionManager.startSession(taskDescriptor.getId())){
            logger.info("--------------------------------------------------------------------SESSION ACQUIRED");
            TransactionOptions txOptions = TransactionOptions.builder().build();
            clientSession.getClientSession().withTransaction(getTransactionBody(operation), txOptions);
            logger.info("--------------------------------------------------------------------EXECUTION OK");
            return Result.OK();
        } catch (Exception ex) {
            logger.info("--------------------------------------------------------------------EXECUTION ERROR");
            logger.warn(ex.getMessage());
            return new Result.Error(ex);
        }
    }

    private TransactionBody<String> getTransactionBody(Runnable operation) {
        return () -> {
            operation.run();
            return "Mongock transaction operation";
        };
    }

}
