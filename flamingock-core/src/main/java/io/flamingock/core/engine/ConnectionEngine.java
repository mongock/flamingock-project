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

package io.flamingock.core.engine;

import io.flamingock.commons.utils.JsonObjectMapper;
import io.flamingock.commons.utils.http.Http;
import io.flamingock.core.cloud.CloudConnectionEngine;
import io.flamingock.core.cloud.transaction.CloudTransactioner;
import io.flamingock.core.configurator.cloud.CloudConfigurable;
import io.flamingock.core.configurator.core.CoreConfigurable;
import io.flamingock.core.configurator.local.LocalConfigurable;
import io.flamingock.core.engine.execution.ExecutionPlanner;
import io.flamingock.commons.utils.RunnerId;
import io.flamingock.core.engine.local.LocalConnectionEngine;
import io.flamingock.core.engine.local.driver.ConnectionDriver;
import io.flamingock.core.transaction.TransactionWrapper;
import org.apache.http.impl.client.HttpClients;

import java.util.Optional;

public interface ConnectionEngine  extends AutoCloseable{

  void initialize(RunnerId runnerId);

  ExecutionPlanner getExecutionPlanner();

  Optional<?  extends TransactionWrapper> getTransactionWrapper();


  static CloudConnectionEngine initializeAndGetCloud(RunnerId runnerId,
                                                     CoreConfigurable coreConfiguration,
                                                     CloudConfigurable cloudConfiguration,
                                                     CloudTransactioner transactioner) {
      CloudConnectionEngine connectionEngine = new CloudConnectionEngine(
              coreConfiguration,
              cloudConfiguration,
              Http.builderFactory(HttpClients.createDefault(), JsonObjectMapper.DEFAULT_INSTANCE),
              transactioner
      );
      connectionEngine.initialize(runnerId);
      return connectionEngine;
  }

  static LocalConnectionEngine initializeAndGetLocal(RunnerId runnerId,
                                                                ConnectionDriver<?> driver,
                                                                CoreConfigurable coreConfiguration,
                                                                LocalConfigurable localConfiguration) {
      LocalConnectionEngine connectionEngine = driver.getConnectionEngine(coreConfiguration, localConfiguration);
      connectionEngine.initialize(runnerId);
      return connectionEngine;
  }
}
