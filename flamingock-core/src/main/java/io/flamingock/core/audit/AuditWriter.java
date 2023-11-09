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

package io.flamingock.core.audit;

import io.flamingock.core.audit.writer.AuditEntry;
import io.flamingock.core.audit.writer.AuditEntryMapper;
import io.flamingock.core.audit.domain.AuditItem;
import io.flamingock.core.util.Result;

/**
 * This class implements the Facade pattern containing the responsibility to log the taskStep, map it to Entry
 * and log then entry just in order to avoid having too many classes to implement that depend on the
 * same AuditEntry implementation, which would enforce to add the generic to the holder/orchestrator class.
 * <br />
 * However, the `mapper responsibility` is intended to be delegated to a Mapper class, but that's is left to decide
 * to the developer implementing this abstract class.
 */
public interface AuditWriter {

    default Result writeStep(AuditItem auditItem) {
        return writeEntry(AuditEntryMapper.map(auditItem));
    }

    Result writeEntry(AuditEntry auditEntry);


}
