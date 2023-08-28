package io.flamingock.core.runner;

import io.flamingock.core.audit.AuditWriter;
import io.flamingock.core.audit.single.SingleAuditReader;
import io.flamingock.core.configurator.CoreConfiguration;
import io.flamingock.core.event.EventPublisher;
import io.flamingock.core.lock.LockAcquirer;
import io.flamingock.core.runtime.dependency.DependencyContext;
import io.flamingock.core.stage.DefinitionStage;
import io.flamingock.core.stage.execution.StageExecutionContext;
import io.flamingock.core.stage.execution.StageExecutor;
import io.flamingock.core.transaction.TransactionWrapper;
import io.flamingock.core.util.StringUtil;

public final class RunnerCreator {

    private RunnerCreator() {
    }

    private static <CORE_CONFIG extends CoreConfiguration> StageExecutionContext buildExecutionContext(CORE_CONFIG configuration) {
        return new StageExecutionContext(StringUtil.executionId(), StringUtil.hostname(), configuration.getDefaultAuthor(), configuration.getMetadata());
    }

    public static Runner create(DefinitionStage definitionStage,
                                SingleAuditReader auditReader,
                                AuditWriter auditWriter,
                                TransactionWrapper transactionWrapper,
                                LockAcquirer lockAcquirer,
                                CoreConfiguration coreConfiguration,
                                EventPublisher eventPublisher,
                                DependencyContext dependencyContext,
                                boolean isThrowExceptionIfCannotObtainLock) {
        //Instantiated here, so we don't wait until Runner.run() and fail fast
        final StageExecutor stageExecutor = StageExecutor.getSequentialStageExecutor(dependencyContext, auditWriter, transactionWrapper);
        return new AbstractRunner(lockAcquirer, auditReader, stageExecutor, buildExecutionContext(coreConfiguration), eventPublisher, isThrowExceptionIfCannotObtainLock) {
            @Override
            public void run() {
                this.run(definitionStage);
            }
        };
    }

}
