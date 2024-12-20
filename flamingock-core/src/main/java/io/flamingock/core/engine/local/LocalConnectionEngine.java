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

package io.flamingock.core.engine.local;

import io.flamingock.commons.utils.RunnerId;
import io.flamingock.core.api.LocalSystemModule;
import io.flamingock.core.api.exception.FlamingockException;
import io.flamingock.core.configurator.core.CoreConfigurable;
import io.flamingock.core.configurator.core.CoreConfiguration;
import io.flamingock.core.engine.ConnectionEngine;

import java.util.Optional;

public interface LocalConnectionEngine extends ConnectionEngine {
    void initialize(RunnerId runnerId);

    Auditor getAuditor();

    Optional<? extends LocalSystemModule> getMongockLegacyImporterModule();

    default void validate(CoreConfigurable coreConfiguration) {
        Boolean transactionEnabled = coreConfiguration.getTransactionEnabled();
        if(getTransactionWrapper().isPresent() && transactionEnabled != null && transactionEnabled) {
            throw new FlamingockException("[transactionEnabled = true] and driver is not transactional");
        }
    }

}
