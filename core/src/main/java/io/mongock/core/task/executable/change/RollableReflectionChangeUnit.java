package io.mongock.core.task.executable.change;

import io.mongock.core.task.descriptor.ReflectionTaskDescriptor;
import io.mongock.core.task.executable.AbstractRollableTask;
import io.mongock.core.runtime.DefaultRuntimeHelper;

import java.lang.reflect.Method;

/**
 * This class is a reflection version of the changeUnit.
 * </br>
 * It creates a new instance on demand in every execution(execution and rollback), because it's intended to be executed
 * just once. The only case it will be potentially executed twice is if it fails, and in that case will only happen
 * once(in case of sequential execution) or very few times(in case or parallel execution and happen to fail multiple
 * concurrent tasks at the same time),because after that the process will abort.
 * </br>
 * For this reason it's more optimal to do it on demand, that articulates some synchronisation mechanism.
 * </br>
 * However, the methods are extracted in advance, so we can spot wrong configuration before starting the process and
 * fail fast.
 */
public class RollableReflectionChangeUnit extends AbstractRollableTask<ReflectionTaskDescriptor, ReflectionExecutableChangeUnit> implements ExecutableChangeUnit {
    private final Method rollbackMethod;


    public RollableReflectionChangeUnit(ReflectionExecutableChangeUnit baseChangeUnit, Method rollbackMethod) {
        super(baseChangeUnit);
        this.rollbackMethod = rollbackMethod;

    }

    @Override
    public String getOrder() {
        return baseTask.getOrder();
    }


    @Override
    public void rollback(DefaultRuntimeHelper runtimeHelper) {
        runtimeHelper.executeMethod(runtimeHelper.getInstance(getDescriptor().getSource()), rollbackMethod);
    }

    @Override
    public String getExecutionMethodName() {
        return baseTask.getExecutionMethodName();
    }

    @Override
    public String getRollbackMethodName() {
        return rollbackMethod.getName();
    }
}
