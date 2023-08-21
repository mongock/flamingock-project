package io.flamingock.core.core.task.executable.change.reflection;

import io.flamingock.core.core.execution.summary.DefaultStepSummarizer;
import io.flamingock.core.core.runtime.RuntimeManager;
import io.flamingock.core.core.task.descriptor.reflection.SortedReflectionTaskDescriptor;
import io.flamingock.core.core.task.executable.AbstractExecutableTask;
import io.flamingock.core.core.task.executable.ExecutableTask;
import io.flamingock.core.core.task.executable.Rollback;
import io.flamingock.core.core.task.executable.change.ExecutableChangeUnit;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

/**
 * This class is a reflection version of the changeUnit.
 * </br>
 * It creates a new instance on demand in every execution(execution and rollback), because it's intended to be executed
 * just once. The only case it will be potentially executed twice is if it fails, and in that case will only happen
 * once(in case of sequential execution) or very few times(in case or parallel execution and happen to fail multiple
 * concurrent tasks at the same time),because after that the process will abort.
 * </br>
 * For this reason it's more optimal to do it on demand, that articulate some synchronisation mechanism.
 * </br>
 * However, the methods are extracted in advance, so we can spot wrong configuration before starting the process and
 * fail fast.
 */
public class ReflectionExecutableChangeUnit extends AbstractExecutableTask<SortedReflectionTaskDescriptor> implements ExecutableChangeUnit {

    private final Method executionMethod;

    private final Method rollbackMethod;

    private final List<Rollback> dependentTasks;



    public ReflectionExecutableChangeUnit(SortedReflectionTaskDescriptor descriptor,
                                          boolean requiredExecution,
                                          Method executionMethod) {
        this(descriptor, requiredExecution, executionMethod, null);

    }

    public ReflectionExecutableChangeUnit(SortedReflectionTaskDescriptor descriptor,
                                          boolean requiredExecution,
                                          Method executionMethod,
                                          Method rollbackMethod) {
        super(descriptor, requiredExecution);
        this.executionMethod = executionMethod;
        this.rollbackMethod = rollbackMethod;
        dependentTasks = new LinkedList<>();

    }


    @Override
    public void addDependentRollbacks(Rollback dependentTask) {
        dependentTasks.add(dependentTask);

    }

    @Override
    public List<? extends Rollback> getDependentTasks() {
        return dependentTasks;
    }

    @Override
    public void execute(RuntimeManager runtimeHelper) {
        runtimeHelper.executeMethod(runtimeHelper.getInstance(descriptor.getSource()), executionMethod);
    }

    @Override
    public Optional<Rollback> getRollback() {
        if(rollbackMethod != null) {
            return Optional.of(buildRollBack());
        } else {
            return Optional.empty();
        }
    }

    @Override
    public String getExecutionMethodName() {
        return executionMethod.getName();
    }

    private Rollback buildRollBack() {
        return new Rollback() {
            @Override
            public ExecutableTask getTask() {
                return ReflectionExecutableChangeUnit.this;
            }

            @Override
            public void rollback(RuntimeManager runtimeHelper) {
                runtimeHelper.executeMethod(runtimeHelper.getInstance(descriptor.getSource()), rollbackMethod);
            }

            @Override
            public String getRollbackMethodName() {
                return rollbackMethod.getName();
            }
        };
    }

}
