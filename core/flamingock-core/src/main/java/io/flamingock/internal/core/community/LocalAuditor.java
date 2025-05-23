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

package io.flamingock.internal.core.community;


import io.flamingock.commons.utils.Result;
import io.flamingock.internal.core.engine.audit.AuditReader;
import io.flamingock.internal.core.engine.audit.AuditWriter;
import io.flamingock.internal.core.engine.audit.domain.ExecutionAuditItem;
import io.flamingock.internal.core.engine.audit.domain.RollbackAuditItem;
import io.flamingock.internal.core.engine.audit.domain.StartExecutionAuditItem;
import io.flamingock.internal.core.engine.audit.writer.AuditEntryMapper;

public interface LocalAuditor extends AuditWriter, AuditReader {

    default Result writeStartExecution(StartExecutionAuditItem auditItem) {
        return Result.OK();
    }

    default Result writeExecution(ExecutionAuditItem auditItem) {
        return writeEntry(AuditEntryMapper.map(auditItem));
    }

    default Result writeRollback(RollbackAuditItem auditItem) {
        return writeEntry(AuditEntryMapper.map(auditItem));
    }

}
