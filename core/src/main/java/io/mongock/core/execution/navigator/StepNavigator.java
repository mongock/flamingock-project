package io.mongock.core.execution.navigator;

import io.mongock.core.audit.writer.AuditWriter;
import io.mongock.core.execution.summary.StepSummarizer;
import io.mongock.core.runtime.RuntimeHelper;

public class StepNavigator extends AbstractStepNavigator {


    StepNavigator(AuditWriter<?> auditWriter,
                  StepSummarizer summarizer,
                  RuntimeHelper runtimeHelper) {
        super(auditWriter, summarizer, runtimeHelper);
    }



}
