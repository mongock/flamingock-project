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

import io.flamingock.core.driver.CloudConnectionEngine;

import java.util.function.Supplier;

public class CloudConfiguratorDelegate<HOLDER> implements CloudConfigurator<HOLDER> {

    private final Supplier<HOLDER> holderSupplier;

    private final CloudConfiguration cloudConfiguration;


    public CloudConfiguratorDelegate(CloudConfiguration cloudConfiguration, Supplier<HOLDER> holderSupplier) {
        this.holderSupplier = holderSupplier;
        this.cloudConfiguration = cloudConfiguration;

    }

    @Override
    public HOLDER setApiKey(String apiKey) {
        cloudConfiguration.setApiKey(apiKey);
        return holderSupplier.get();
    }

    @Override
    public HOLDER setToken(String token) {
        cloudConfiguration.setToken(token);
        return holderSupplier.get();
    }


    public CloudConnectionEngine getAndInitializeConnectionEngine() {
        CloudConnectionEngine connectionEngine = CloudConnectionEngine.getInstance();
        connectionEngine.setConfiguration(cloudConfiguration);
        connectionEngine.initialize();
        return connectionEngine;
    }

}
