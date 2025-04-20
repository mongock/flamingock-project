/*
 * Copyright 2023 Flamingock (https://oss.flamingock.io)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.flamingock.core.task.executable;

import io.flamingock.core.api.template.ChangeTemplate;
import io.flamingock.core.runtime.RuntimeManager;
import io.flamingock.core.task.loaded.TemplateLoadedChangeUnit;
import io.flamingock.commons.utils.FileUtil;
import io.flamingock.commons.utils.ReflectionUtil;

import java.lang.reflect.Method;
import java.util.List;

public class TemplateExecutableTask extends ReflectionExecutableTask<TemplateLoadedChangeUnit> {


    public TemplateExecutableTask(String stageName,
                                  TemplateLoadedChangeUnit descriptor,
                                  boolean requiredExecution,
                                  Method executionMethod,
                                  Method rollbackMethod) {
        super(stageName, descriptor, requiredExecution, executionMethod, rollbackMethod);
    }

    //TODO cache the instance locally, as it's stateless
    @Override
    protected void executeInternal(RuntimeManager runtimeManager, Method method ) {
        Object instance = runtimeManager.getInstance(descriptor.getConstructor());
        setConfiguration(runtimeManager, (ChangeTemplate<?>) instance);
        runtimeManager.executeMethodWithInjectedDependencies(instance, method);
    }


    private void setConfiguration(RuntimeManager runtimeManager, ChangeTemplate<?> instance) {
        try {
            Class<?> configClass = instance.getConfigClass();
            Method setConfigurationMethod = instance.getClass().getMethod("setConfiguration", Object.class);
            runtimeManager.executeMethodWithParameters(
                    instance,
                    setConfigurationMethod,
                    FileUtil.getFromMap(configClass, descriptor.getTemplateConfiguration()));
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }




}
