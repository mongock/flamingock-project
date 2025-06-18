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

package io.flamingock.internal.core.task.loaded;

import io.flamingock.commons.utils.ReflectionUtil;
import io.flamingock.api.annotations.FlamingockConstructor;
import io.flamingock.internal.commons.core.error.FlamingockException;

import java.lang.reflect.Constructor;

public abstract class AbstractLoadedChangeUnit extends AbstractReflectionLoadedTask {



    protected AbstractLoadedChangeUnit(String id,
                                    String order,
                                    Class<?> sourceClass,
                                    boolean runAlways,
                                    boolean transactional,
                                    boolean systemTask) {
        super(id, order, sourceClass, runAlways, transactional, systemTask);
    }

    @Override
    public Constructor<?> getConstructor() {
        try {
            return ReflectionUtil.getConstructorWithAnnotationPreference(getSourceClass(), FlamingockConstructor.class);
        } catch (ReflectionUtil.MultipleAnnotatedConstructorsFound ex) {
            throw new FlamingockException("Found multiple constructors for class[%s] annotated with %s." +
                    " Annotate the one you want Flamingock to use to instantiate your changeUnit",
                    getSource(),
                    FlamingockConstructor.class.getName());
        } catch (ReflectionUtil.MultipleConstructorsFound ex) {
            throw new FlamingockException("Found multiple constructors, please provide at least one  for class[%s].\n" +
                    "When more than one constructor, exactly one of them must be annotated. And it will be taken as default "
                    , FlamingockConstructor.class.getSimpleName()
                    , getSource()
            );
        } catch (ReflectionUtil.ConstructorNotFound ex) {
            throw new FlamingockException("Cannot find a valid constructor for class[%s]", getSource());
        }
    }
}
