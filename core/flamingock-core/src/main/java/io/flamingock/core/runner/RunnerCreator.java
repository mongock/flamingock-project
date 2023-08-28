package io.flamingock.core.runner;

import io.flamingock.core.Factory;
import io.flamingock.core.configurator.CoreConfiguration;
import io.flamingock.core.event.EventPublisher;
import io.flamingock.core.runtime.dependency.DependencyContext;
import io.flamingock.core.stage.DefinitionStage;
import io.flamingock.core.stage.execution.StageExecutionContext;
import io.flamingock.core.stage.execution.StageExecutor;
import io.flamingock.core.util.StringUtil;

public final class RunnerCreator {

    private RunnerCreator() {
    }

    private static <CORE_CONFIG extends CoreConfiguration> StageExecutionContext buildExecutionContext(CORE_CONFIG configuration) {
        return new StageExecutionContext(
                StringUtil.executionId(),
                StringUtil.hostname(),
                configuration.getDefaultAuthor(),
                configuration.getMetadata()
        );
    }

    public static <CONFIGURATION> Runner create(Factory<CONFIGURATION> factory,
                                                CoreConfiguration coreConfiguration,
                                                CONFIGURATION extraProperties,
                                                EventPublisher eventPublisher,
                                                DependencyContext dependencyContext,
                                                boolean isThrowExceptionIfCannotObtainLock) {
        //Instantiated here, so we don't wait until Runner.run() and fail fast
        final DefinitionStage definitionStage = factory.getDefinitionProcess(extraProperties);
        final StageExecutor stageExecutor = StageExecutor.getSequentialStageExecutor(
                dependencyContext,
                factory.getAuditWriter(),
                factory.getTransactionWrapper().orElse(null));
        return new AbstractRunner(
                factory.getLockAcquirer(),
                factory.getAuditReader(),
                stageExecutor,
                buildExecutionContext(coreConfiguration),
                eventPublisher,
                isThrowExceptionIfCannotObtainLock) {
            @Override
            public void run() {
                this.run(definitionStage);
            }
        };
    }

}
