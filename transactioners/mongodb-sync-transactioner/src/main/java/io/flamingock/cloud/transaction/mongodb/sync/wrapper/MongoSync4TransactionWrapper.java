/*
 * Copyright 2023 Flamingock (https://oss.flamingock.io)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.flamingock.cloud.transaction.mongodb.sync.wrapper;

import com.mongodb.TransactionOptions;
import com.mongodb.client.ClientSession;
import io.flamingock.internal.common.core.context.Dependency;
import io.flamingock.internal.core.task.navigation.step.FailedStep;
import io.flamingock.internal.common.core.context.DependencyInjectable;
import io.flamingock.internal.common.core.task.TaskDescriptor;
import io.flamingock.internal.core.transaction.TransactionWrapper;
import io.flamingock.internal.core.community.TransactionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Supplier;

public class MongoSync4TransactionWrapper implements TransactionWrapper {
    private static final Logger logger = LoggerFactory.getLogger(MongoSync4TransactionWrapper.class);

    private final TransactionManager<ClientSession> sessionManager;

    public MongoSync4TransactionWrapper(TransactionManager<ClientSession> sessionManager) {
        this.sessionManager = sessionManager;
    }

    @Override
    public <T> T wrapInTransaction(TaskDescriptor loadedTask, DependencyInjectable dependencyInjectable, Supplier<T> operation) {
        String sessionId = loadedTask.getId();
        Dependency clienteSessionDependency = null;
        try (ClientSession clientSession = sessionManager.startSession(sessionId)) {
            clienteSessionDependency = new Dependency(clientSession);
            clientSession.startTransaction(TransactionOptions.builder().build());
            dependencyInjectable.addDependency(clienteSessionDependency);
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
            if(clienteSessionDependency != null) {
                dependencyInjectable.removeDependencyByRef(clienteSessionDependency);
            }
        }
    }


}
