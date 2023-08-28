package io.flamingock.core.stage.execution;

import io.flamingock.core.audit.AuditWriter;
import io.flamingock.core.lock.Lock;
import io.flamingock.core.runtime.dependency.DependencyContext;
import io.flamingock.core.stage.ExecutableStage;
import io.flamingock.core.task.executable.ExecutableTask;
import io.flamingock.core.task.navigation.navigator.ReusableStepNavigatorBuilder;
import io.flamingock.core.task.navigation.navigator.StepNavigationOutput;
import io.flamingock.core.task.navigation.navigator.StepNavigatorBuilder;
import io.flamingock.core.task.navigation.summary.DefaultStepSummarizer;
import io.flamingock.core.task.navigation.summary.ProcessSummary;
import io.flamingock.core.task.navigation.summary.StepSummary;
import io.flamingock.core.transaction.TransactionWrapper;
import io.flamingock.core.util.StreamUtil;

import java.util.Optional;
import java.util.stream.Stream;

public class StageExecutor {
    protected final AuditWriter auditWriter;

    protected final TransactionWrapper transactionWrapper;
    private final DependencyContext dependencyContext;
    private final boolean parallel;

    private StageExecutor(DependencyContext dependencyContext, AuditWriter auditWriter, boolean parallel) {
        this(dependencyContext, auditWriter, parallel, null);
    }

    private StageExecutor(DependencyContext dependencyContext, AuditWriter auditWriter, boolean parallel, TransactionWrapper transactionWrapper) {
        this.dependencyContext = dependencyContext;
        this.auditWriter = auditWriter;
        this.parallel = parallel;
        this.transactionWrapper = transactionWrapper;
    }

    public static StageExecutor getSequentialStageExecutor(DependencyContext dependencyContext, AuditWriter auditWriter) {
        return getParallelStageExecutor(dependencyContext, auditWriter, null);
    }

    public static StageExecutor getSequentialStageExecutor(DependencyContext dependencyContext, AuditWriter auditWriter, TransactionWrapper transactionWrapper) {
        return getStageExecutor(dependencyContext, auditWriter, false, transactionWrapper);
    }

    public static StageExecutor getParallelStageExecutor(DependencyContext dependencyContext, AuditWriter auditWriter) {
        return getParallelStageExecutor(dependencyContext, auditWriter, null);
    }

    public static StageExecutor getParallelStageExecutor(DependencyContext dependencyContext, AuditWriter auditWriter, TransactionWrapper transactionWrapper) {
        return getStageExecutor(dependencyContext, auditWriter, true, transactionWrapper);
    }

    private static StageExecutor getStageExecutor(DependencyContext dependencyContext, AuditWriter auditWriter, boolean parallel, TransactionWrapper transactionWrapper) {
        return new StageExecutor(dependencyContext, auditWriter, parallel, transactionWrapper);
    }

    public Output execute(ExecutableStage executableProcess, StageExecutionContext stageExecutionContext, Lock lock) throws StageExecutionException {

        ProcessSummary summary = new ProcessSummary();

        StepNavigatorBuilder stepNavigatorBuilder = getStepNavigatorBuilder();

        //TODO think that we can build the StepNavigator sequentially and then execute it in Parallel
        // this would save memory footprint
        Stream<StepNavigationOutput> taskStepStream = getTaskStream(executableProcess).map(task -> stepNavigatorBuilder.setAuditWriter(auditWriter).setStaticContext(dependencyContext).setLock(lock).setTransactionWrapper(transactionWrapper).setSummarizer(new DefaultStepSummarizer())//todo reuse Summarizer
                .build().executeTask(task, stageExecutionContext)).peek(summary::addSummary);

        try {
            Optional<StepNavigationOutput> failedOutput = StreamUtil.processUntil(taskStepStream, StepNavigationOutput::isFailed);
            failedOutput.ifPresent(failed -> {throw new StageExecutionException(summary);});

        } catch (Throwable throwable) {
            throw new StageExecutionException(throwable, summary);
        }

        return new Output(summary);
    }

    protected Stream<? extends ExecutableTask> getTaskStream(ExecutableStage executableStage) {
        return parallel ? executableStage.getTasks().parallelStream() : executableStage.getTasks().stream();
    }

    protected StepNavigatorBuilder getStepNavigatorBuilder() {
        StepNavigatorBuilder immutableStepNavigatorBuilder = null;
        return parallel ? immutableStepNavigatorBuilder //TODO  implement ConcurrentStepNavigatorBuilder
                : new ReusableStepNavigatorBuilder();
    }

    public static class Output {

        private final StepSummary summary;

        public Output(StepSummary summary) {
            this.summary = summary;
        }

        public StepSummary getSummary() {
            return summary;
        }
    }
}
