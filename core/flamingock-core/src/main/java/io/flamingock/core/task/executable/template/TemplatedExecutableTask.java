package io.flamingock.core.task.executable.template;

import io.flamingock.core.api.annotations.template.FlamingockTemplate;
import io.flamingock.core.runtime.RuntimeManager;
import io.flamingock.core.runtime.dependency.Dependency;
import io.flamingock.core.task.descriptor.TemplatedTaskDescriptor;
import io.flamingock.core.task.executable.ReflectionExecutableTask;

import java.lang.reflect.Method;

public class TemplatedExecutableTask extends ReflectionExecutableTask<TemplatedTaskDescriptor> {
    public TemplatedExecutableTask(TemplatedTaskDescriptor descriptor,
                                   boolean requiredExecution,
                                   Method executionMethod,
                                   Method rollbackMethod) {
        super(descriptor, requiredExecution, executionMethod, rollbackMethod);
    }

    @Override
    protected void executeInternal(RuntimeManager runtimeManager, Object instance, Method method ) {
//        runtimeManager.executeMethod(instance, method);
        runtimeManager.addDependency(new Dependency(descriptor.getTemplateConfiguration()));
        ((FlamingockTemplate)instance).setConfiguration(descriptor.getTemplateConfiguration());
        runtimeManager.executeMethod(instance, method);
    }
}
