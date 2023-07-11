package io.flamingock.core.core.task.executable.change;

import io.flamingock.core.core.runtime.RuntimeManager;
import io.flamingock.core.core.task.descriptor.impl.ReflectionTaskDescriptor;
import io.flamingock.core.core.task.executable.AbstractExecutableTask;
import io.flamingock.core.core.task.executable.RollableTask;

import java.lang.reflect.Method;

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
public class ReflectionExecutableChangeUnit extends AbstractExecutableTask<ReflectionTaskDescriptor> implements ExecutableChangeUnit {

    private final String order;
    private final Method executionMethod;


    public ReflectionExecutableChangeUnit(ReflectionTaskDescriptor descriptor,
                                          String order,
                                          boolean requiredExecution,
                                          Method executionMethod) {
        super(descriptor, requiredExecution);
        this.order = order;
        this.executionMethod = executionMethod;

    }

    @Override
    public String getOrder() {
        return order;
    }

    @Override
    public void addRollbackDependent(RollableTask rollbackDependent) {

    }

    @Override
    public void execute(RuntimeManager runtimeHelper) {
        runtimeHelper.executeMethod(runtimeHelper.getInstance(descriptor.getSource()), executionMethod);
    }

    @Override
    public String getExecutionMethodName() {
        return executionMethod.getName();
    }

}
