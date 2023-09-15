package io.flamingock.core.task.executable.template;

import io.flamingock.core.runtime.RuntimeManager;
import io.flamingock.core.runtime.dependency.Dependency;
import io.flamingock.core.task.descriptor.TemplatedTaskDescriptor;
import io.flamingock.core.task.executable.ReflectionExecutableTask;
import io.flamingock.core.util.FileUtil;
import io.flamingock.core.util.Pair;
import io.flamingock.core.util.ReflectionUtil;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Optional;

public class TemplatedExecutableTask extends ReflectionExecutableTask<TemplatedTaskDescriptor> {
    private final Method configValidatorMethod;

    private final Method configSetterMethod;


    public TemplatedExecutableTask(TemplatedTaskDescriptor descriptor,
                                   boolean requiredExecution,
                                   Method executionMethod,
                                   Method rollbackMethod,
                                   Method configSetterMethod,
                                   Method configValidatorMethod) {
        super(descriptor, requiredExecution, executionMethod, rollbackMethod);
        this.configSetterMethod = configSetterMethod;
        this.configValidatorMethod = configValidatorMethod;
    }

    @Override
    protected void executeInternal(RuntimeManager runtimeManager, Method method ) {
        Object instance = runtimeManager.getInstance(descriptor.getSourceClass());
        setConfiguration(runtimeManager, instance);
        runtimeManager.executeMethod(instance, method);
    }

    private void setConfiguration(RuntimeManager runtimeManager, Object instance) {
        if(configSetterMethod != null ) {
            List<Class<?>> parameters = ReflectionUtil.getParameters(configSetterMethod);
            if(!parameters.isEmpty()) {
                Dependency dependency = new Dependency(
                        parameters.get(0),
                        FileUtil.getFromMap(parameters.get(0), descriptor.getTemplateConfiguration()));
                runtimeManager.addDependency(dependency);
            }
            runtimeManager.executeMethod(instance, configSetterMethod);
        }

        validateConfiguration(runtimeManager, instance);
    }

    private void validateConfiguration(RuntimeManager runtimeManager, Object instance) {
        if(configValidatorMethod != null) {
            runtimeManager.executeMethod(instance, configValidatorMethod);
        }
    }


}
