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

package io.flamingock.core.engine.local.driver;

import io.flamingock.commons.utils.RunnerId;
import io.flamingock.core.configurator.core.CoreConfigurable;
import io.flamingock.core.configurator.local.LocalConfigurable;
import io.flamingock.core.engine.local.LocalConnectionEngine;

public interface ConnectionDriver<DRIVER_CONFIGURATION extends DriverConfigurable> {

    LocalConnectionEngine getConnectionEngine(RunnerId runnerId,
                                              CoreConfigurable coreConfiguration,
                                              LocalConfigurable communityConfiguration);


    ConnectionDriver<DRIVER_CONFIGURATION> setDriverConfiguration(DRIVER_CONFIGURATION configuration);
}
