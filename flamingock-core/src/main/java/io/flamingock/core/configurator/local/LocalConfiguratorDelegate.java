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

package io.flamingock.core.configurator.local;

import flamingock.core.api.CloudSystemModule;
import flamingock.core.api.LocalSystemModule;
import io.flamingock.core.engine.local.driver.ConnectionDriver;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Supplier;

public class LocalConfiguratorDelegate<HOLDER> implements LocalConfigurator<HOLDER> {

    private final LocalConfigurable LocalConfiguration;

    private final Supplier<HOLDER> holderSupplier;

    private ConnectionDriver<?> connectionDriver;

    private List<LocalSystemModule> systemModules = new LinkedList<>();


    public LocalConfiguratorDelegate(LocalConfigurable communityConfiguration, Supplier<HOLDER> holderSupplier) {
        this.LocalConfiguration = communityConfiguration;
        this.holderSupplier = holderSupplier;

    }

    @Override
    public HOLDER setDriver(ConnectionDriver<?> connectionDriver) {
        this.connectionDriver = connectionDriver;
        return holderSupplier.get();
    }

    @Override
    public ConnectionDriver<?> getDriver() {
        return connectionDriver;
    }

    @Override
    public LocalConfigurable getLocalConfiguration() {
        return LocalConfiguration;
    }

    @Override
    public HOLDER addSystemModule(LocalSystemModule systemModule) {
        systemModules.add(systemModule);
        return holderSupplier.get();
    }

    @Override
    public Iterable<LocalSystemModule> getSystemModules() {
        return systemModules;
    }
}
