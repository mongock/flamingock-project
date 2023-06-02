package io.flamingock.oss.core.execution.executor;

import io.flamingock.oss.core.audit.writer.AuditWriter;
import io.flamingock.oss.core.execution.navigator.ReusableStepNavigatorBuilder;
import io.flamingock.oss.core.execution.navigator.StepNavigatorBuilder;
import io.flamingock.oss.core.process.single.SingleExecutableProcess;
import io.flamingock.oss.core.task.executable.ExecutableTask;
import io.flamingock.oss.core.transaction.TransactionWrapper;
import io.flamingock.oss.core.runtime.dependency.DependencyContext;

import java.util.stream.Stream;

public class SingleProcessExecutor extends AbstractSingleProcessExecutor {

    public SingleProcessExecutor(DependencyContext dependencyManager,
                                 AuditWriter<?> auditWriter) {
        this(dependencyManager, auditWriter, null);
    }

    public SingleProcessExecutor(DependencyContext dependencyManager,
                                 AuditWriter<?> auditWriter,
                                 TransactionWrapper transactionWrapper) {
        super(dependencyManager, auditWriter, transactionWrapper);
    }

    @Override
    protected Stream<? extends ExecutableTask> buildTaskStream(SingleExecutableProcess executableProcess) {
        return executableProcess.getTasks().stream();
    }

    @Override
    protected StepNavigatorBuilder getStepNavigatorBuilder() {
        return new ReusableStepNavigatorBuilder();
    }

}
