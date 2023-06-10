package io.flamingock.cloud.executor;

import io.flamingock.core.core.audit.AuditWriter;
import io.flamingock.core.core.execution.executor.AbstractSingleProcessExecutor;
import io.flamingock.core.core.execution.navigator.StepNavigatorBuilder;
import io.flamingock.core.core.process.single.SingleExecutableProcess;
import io.flamingock.core.core.runtime.dependency.DependencyContext;
import io.flamingock.core.core.task.executable.ExecutableTask;
import io.flamingock.core.core.transaction.TransactionWrapper;

import java.util.stream.Stream;

public class ParallelProcessExecutor extends AbstractSingleProcessExecutor {

    public ParallelProcessExecutor(DependencyContext dependencyManager,
                                   AuditWriter auditWriter) {
        this(dependencyManager, auditWriter, null);
    }

    public ParallelProcessExecutor(DependencyContext dependencyManager,
                                   AuditWriter auditWriter,
                                   TransactionWrapper transactionWrapper) {
        super(dependencyManager, auditWriter, transactionWrapper);
    }

    @Override
    protected Stream<? extends ExecutableTask> buildTaskStream(SingleExecutableProcess executableProcess) {
        return executableProcess.getTasks().parallelStream();
    }

    @Override
    protected StepNavigatorBuilder getStepNavigatorBuilder() {
        return new ParallelStepNavigatorBuilder();
    }

}
