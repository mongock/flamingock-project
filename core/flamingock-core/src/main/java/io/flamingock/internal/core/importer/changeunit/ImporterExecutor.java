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

package io.flamingock.internal.core.importer.changeunit;


import io.flamingock.internal.core.engine.audit.AuditWriter;
import io.flamingock.internal.core.importer.ImporterReader;
import io.flamingock.internal.core.engine.audit.domain.AuditEntry;
import io.flamingock.internal.core.pipeline.PipelineDescriptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This changeUnit imports the Mongock data in the database to Flamingock(local or cloud).
 * Although we could have just one ChangeUnit for importing
 * - Mongock            to Flamingock local
 * - Mongock            to Flamingock CLoud
 * - Flamingock local   to Flamingock cloud
 * We need to differentiate it, as we can have two steps(Mongock to Flamingock local to Flamingock Cloud)
 */
public final class ImporterExecutor {
    private static final String errorTemplate = "importing changeUnit with id[%s] from database. It must be imported  to a flamingock stage";
    private static final Logger logger = LoggerFactory.getLogger(ImporterExecutor.class);

    private ImporterExecutor() {
    }

    /**
     * Reads from a database (either Mongock or Flamingock local) and writes to Flamingock (either local or cloud).
     * The supported migration paths are:
     * - Mongock            to Flamingock local
     * - Mongock            to Flamingock Cloud
     * - Flamingock local   to Flamingock Cloud
     * @param importerReader     Database log reader.
     * @param auditWriter        Destination writer.
     * @param pipelineDescriptor Structure containing all information about the changes and tasks to execute.
     */
    public static void runImport(ImporterReader importerReader,
                                 AuditWriter auditWriter,
                                 PipelineDescriptor pipelineDescriptor) {

        boolean fromMongockDb = importerReader.isFromMongock();

        logStarting(fromMongockDb, importerReader.getSourceDescription(), auditWriter.isCloud());

        importerReader.getAuditEntries().forEach(auditEntryFomDb -> {
            //This is the taskId present in the pipeline(if it's a system change), as '..._before' won't appear
            String taskIdInPipeline = getTaskIdToLookForInPipeline(auditEntryFomDb, fromMongockDb);
            String transformedTaskIdToBeStored = getTransformedTaskIdToBeStored(auditEntryFomDb, fromMongockDb);
            String stageId = getStageId(pipelineDescriptor, auditEntryFomDb, taskIdInPipeline);
            AuditEntry auditEntryWithStageId = auditEntryFomDb.copyWithNewIdAndStageId(transformedTaskIdToBeStored, stageId);
            auditWriter.writeEntry(auditEntryWithStageId);
        });
    }

    private static String getStageId(PipelineDescriptor pipelineDescriptor, AuditEntry auditEntryFomDb, String taskId) {
        if (Boolean.TRUE.equals(auditEntryFomDb.getSystemChange())) {
            return "mongock-legacy-system-changes";
        } else {
            return pipelineDescriptor.getStageByTask(taskId).orElseThrow(() -> new IllegalArgumentException(String.format(errorTemplate, getBaseTaskId(auditEntryFomDb))));
        }
    }

    private static String getBaseTaskId(AuditEntry auditEntry) {
        String originalTaskId = auditEntry.getTaskId();
        int index = originalTaskId.indexOf("_before");
        return index >= 0 ? originalTaskId.substring(0, index) : originalTaskId;
    }

    private static String getTaskIdToLookForInPipeline(AuditEntry auditEntry, boolean fromMongockDb) {
        return getBaseTaskId(auditEntry);
    }


    private static String getTransformedTaskIdToBeStored(AuditEntry auditEntry, boolean fromMongockDb) {
        return auditEntry.getTaskId();
    }

    private static void logStarting(boolean fromMongockDb, String sourceDescription, boolean isCloud) {
        logger.info("Importing audit log from {}'s database[[source= {} ]] to Flamingock[{}]",
                fromMongockDb ? "Mongock" : "Flamingock local",
                sourceDescription,
                isCloud ? "Cloud" : "local");
    }
}
