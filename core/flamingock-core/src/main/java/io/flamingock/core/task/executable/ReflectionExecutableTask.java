package io.flamingock.core.task.executable;

import io.flamingock.core.runtime.RuntimeManager;
import io.flamingock.core.task.descriptor.ReflectionTaskDescriptor;

import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;

/**
 * This class is a reflection version of the ExecutableTask.
 * <p>
 * It creates a new instance on demand in every execution(execution and rollback), because it's intended to be executed
 * just once. The only case it will be potentially executed twice is if it fails, and in that case will only happen
 * once(in case of sequential execution) or very few times(in case or parallel execution and happen to fail multiple
 * concurrent tasks at the same time),because after that the process will abort.
 * <p>
 * For this reason it's more optimal to do it on demand, that articulate some synchronisation mechanism.
 * <p>
 * However, the methods are extracted in advance, so we can spot wrong configuration before starting the process and
 * fail fast.
 */
public class ReflectionExecutableTask<REFLECTION_TASK_DESCRIPTOR extends ReflectionTaskDescriptor> extends AbstractExecutableTask<REFLECTION_TASK_DESCRIPTOR> implements ExecutableTask {

    protected final Method executionMethod;

    protected final List<Rollback> rollbackChain;


    public ReflectionExecutableTask(REFLECTION_TASK_DESCRIPTOR descriptor,
                                    boolean requiredExecution,
                                    Method executionMethod,
                                    Method rollbackMethod) {
        super(descriptor, requiredExecution);
        this.executionMethod = executionMethod;
        rollbackChain = new LinkedList<>();
        if(rollbackMethod != null) {
            rollbackChain.add(buildRollBack(rollbackMethod));
        }
    }

    @Override
    public void addRollback(Rollback rollback) {
        rollbackChain.add(rollback);

    }

    @Override
    public List<? extends Rollback> getRollbackChain() {
        return rollbackChain;
    }

    @Override
    public void execute(RuntimeManager runtimeManager) {
        executeInternal(runtimeManager, runtimeManager.getInstance(descriptor.getSourceClass()), executionMethod);

    }

    protected void executeInternal(RuntimeManager runtimeManager, Object instance, Method method ) {
        runtimeManager.executeMethod(instance, method);
    }



    @Override
    public String getExecutionMethodName() {
        return executionMethod.getName();
    }

    private Rollback buildRollBack(Method rollbackMethod) {
        return new Rollback() {
            @Override
            public ExecutableTask getTask() {
                return ReflectionExecutableTask.this;
            }

            @Override
            public void rollback(RuntimeManager runtimeManager) {
                executeInternal(runtimeManager, runtimeManager.getInstance(descriptor.getSourceClass()), rollbackMethod);
            }

            @Override
            public String getRollbackMethodName() {
                return rollbackMethod.getName();
            }
        };
    }

}
