package io.flamingock.internal.executor;

import io.mongock.core.audit.writer.AuditWriter;
import io.mongock.core.execution.executor.AbstractSingleProcessExecutor;
import io.mongock.core.execution.navigator.StepNavigatorBuilder;
import io.mongock.core.process.single.SingleExecutableProcess;
import io.mongock.core.runtime.dependency.DependencyContext;
import io.mongock.core.task.executable.ExecutableTask;
import io.mongock.core.transaction.TransactionWrapper;

import java.util.stream.Stream;

public class ParallelProcessExecutor extends AbstractSingleProcessExecutor {

    public ParallelProcessExecutor(DependencyContext dependencyManager,
                                   AuditWriter<?> auditWriter) {
        this(dependencyManager, auditWriter, null);
    }

    public ParallelProcessExecutor(DependencyContext dependencyManager,
                                   AuditWriter<?> auditWriter,
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
