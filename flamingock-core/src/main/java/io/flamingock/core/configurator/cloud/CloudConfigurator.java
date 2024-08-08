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

package io.flamingock.core.configurator.cloud;

import flamingock.core.api.CloudSystemModule;
import io.flamingock.core.cloud.transaction.CloudTransactioner;

import java.util.Optional;

public interface CloudConfigurator<HOLDER> {

    HOLDER setHost(String host);

    HOLDER setService(String service);

    HOLDER setEnvironment(String environment);

    HOLDER setApiToken(String clientSecret);

    HOLDER setCloudTransactioner(CloudTransactioner cloudTransactioner);
    Optional<CloudTransactioner> getCloudTransactioner();

    HOLDER addSystemModule(CloudSystemModule systemModule);
    CloudSystemModuleManager getCloudSystemModuleManager();

}
