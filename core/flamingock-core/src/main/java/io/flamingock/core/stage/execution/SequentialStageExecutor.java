package io.flamingock.core.stage.execution;

import io.flamingock.core.audit.AuditWriter;
import io.flamingock.core.task.navigation.navigator.ReusableStepNavigatorBuilder;
import io.flamingock.core.task.navigation.navigator.StepNavigatorBuilder;
import io.flamingock.core.runtime.dependency.DependencyContext;
import io.flamingock.core.stage.ExecutableStage;
import io.flamingock.core.task.executable.ExecutableTask;
import io.flamingock.core.transaction.TransactionWrapper;

import java.util.stream.Stream;

public class SequentialStageExecutor extends StageExecutor {

    public SequentialStageExecutor(DependencyContext dependencyManager,
                                   AuditWriter auditWriter) {
        this(dependencyManager, auditWriter, null);
    }

    public SequentialStageExecutor(DependencyContext dependencyManager,
                                   AuditWriter auditWriter,
                                   TransactionWrapper transactionWrapper) {
        super(dependencyManager, auditWriter, false, transactionWrapper);
    }


    protected Stream<? extends ExecutableTask> getTaskStream(ExecutableStage executableStage) {
        return executableStage.getTasks().stream();
    }

    @Override
    protected StepNavigatorBuilder getStepNavigatorBuilder() {
        return new ReusableStepNavigatorBuilder();
    }

}
