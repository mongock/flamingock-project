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

package io.flamingock.importer.changeunit;


import io.flamingock.core.api.annotations.ChangeUnit;
import io.flamingock.core.api.annotations.Execution;
import io.flamingock.core.api.annotations.NonLockGuarded;
import io.flamingock.core.audit.AuditWriter;
import io.flamingock.importer.ImporterAdapter;
import io.flamingock.core.pipeline.PipelineDescriptor;

/**
 * This changeUnit imports the Mongock data in the database to Flamingock(local or cloud).
 * Although we could have just one ChangeUnit for importing
 * - Mongock            to Flamingock-ce
 * - Mongock            to Flamingock Cloud
 * - Flamingock-ce      to Flamingock Cloud
 * We need to differentiate it, as we can have two steps(Mongock to Flamingock local to Flamingock Cloud)
 */
@ChangeUnit(id = FlamingockLocalImporterChangeUnit.IMPORTER_FROM_FLAMINGOCK_LOCAL, order = "002")
public class FlamingockLocalImporterChangeUnit {
    public static final String IMPORTER_FROM_FLAMINGOCK_LOCAL = "importer-from-flamingock-local";

    @Execution
    public void execution(@NonLockGuarded ImporterAdapter importerReader,
                          @NonLockGuarded AuditWriter auditWriter,
                          @NonLockGuarded PipelineDescriptor pipelineDescriptor) {
        ImporterExecutor.runImport(importerReader, auditWriter, pipelineDescriptor);

    }


}
