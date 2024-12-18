package io.flamingock.cloud.transaction.mongodb.sync.v4;

import com.mongodb.client.ClientSession;
import com.mongodb.client.MongoClient;
import io.flamingock.core.driver.TransactionManager;
import io.flamingock.core.cloud.api.transaction.OngoingStatus;
import io.flamingock.core.cloud.transaction.CloudTransactioner;
import io.flamingock.core.runtime.dependency.DependencyInjectable;
import io.flamingock.core.task.descriptor.TaskDescriptor;

import java.util.Set;
import java.util.function.Supplier;

public class MongoSync4CloudTransactioner implements CloudTransactioner {
    private final MongoClient mongoClient;

    private final String databaseName;

    public MongoSync4CloudTransactioner(MongoClient mongoClient, String databaseName) {
        this.mongoClient = mongoClient;
        this.databaseName = databaseName;
    }

    @Override
    public void initialize() {
        TransactionManager<ClientSession> sessionManager = new TransactionManager<>(mongoClient::startSession);

    }

    @Override
    public void close() {

    }

    @Override
    public Set<OngoingStatus> getOngoingStatuses() {
        return null;
    }

    @Override
    public void cleanOngoingStatus(String taskId) {

    }

    @Override
    public void saveOngoingStatus(OngoingStatus status) {

    }

    @Override
    public <T> T wrapInTransaction(TaskDescriptor taskDescriptor, DependencyInjectable dependencyInjectable, Supplier<T> operation) {
        return null;
    }
}
