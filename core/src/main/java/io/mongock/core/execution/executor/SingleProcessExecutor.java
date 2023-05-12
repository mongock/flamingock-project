package io.mongock.core.execution.executor;

import io.mongock.core.audit.writer.AuditWriter;
import io.mongock.core.execution.navigator.StepNavigationOutput;
import io.mongock.core.execution.navigator.StepNavigatorBuilder;
import io.mongock.core.execution.summary.DefaultStepSummarizer;
import io.mongock.core.execution.summary.ProcessSummary;
import io.mongock.core.lock.Lock;
import io.mongock.core.process.single.SingleExecutableProcess;
import io.mongock.core.runtime.RuntimeManager;
import io.mongock.core.runtime.dependency.AbstractDependencyManager;
import io.mongock.core.runtime.dependency.DependencyContext;
import io.mongock.core.task.executable.ExecutableTask;
import io.mongock.core.transaction.TransactionWrapper;

import java.util.Optional;
import java.util.stream.Stream;

import static io.mongock.core.util.StreamUtil.processUntil;

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
        return new StepNavigatorBuilder.ReusableStepNavigatorBuilder();
    }

}
