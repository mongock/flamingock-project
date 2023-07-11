package io.flamingock.core.core.execution.executor;

import io.flamingock.core.core.audit.AuditWriter;
import io.flamingock.core.core.execution.navigator.ReusableStepNavigatorBuilder;
import io.flamingock.core.core.execution.navigator.StepNavigatorBuilder;
import io.flamingock.core.core.process.single.SingleExecutableProcess;
import io.flamingock.core.core.runtime.dependency.DependencyContext;
import io.flamingock.core.core.task.executable.ExecutableTask;
import io.flamingock.core.core.task.executable.OrderedExecutableTask;
import io.flamingock.core.core.transaction.TransactionWrapper;

import java.util.stream.Stream;

public class SeqSingleProcessExecutor extends AbstractSingleProcessExecutor {

    public SeqSingleProcessExecutor(DependencyContext dependencyManager,
                                    AuditWriter auditWriter) {
        this(dependencyManager, auditWriter, null);
    }

    public SeqSingleProcessExecutor(DependencyContext dependencyManager,
                                    AuditWriter auditWriter,
                                    TransactionWrapper transactionWrapper) {
        super(dependencyManager, auditWriter, transactionWrapper);
    }

    @Override
    protected Stream<? extends ExecutableTask> getTaskStream(SingleExecutableProcess executableProcess) {
        return executableProcess.getTasks().stream();
    }

    @Override
    protected StepNavigatorBuilder getStepNavigatorBuilder() {
        return new ReusableStepNavigatorBuilder();
    }

}
