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

package io.flamingock.core.task.descriptor;

import io.flamingock.commons.utils.ReflectionUtil;
import io.flamingock.core.api.annotations.FlamingockConstructor;
import io.flamingock.core.api.exception.FlamingockException;
import io.mongock.api.annotations.ChangeUnitConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.util.List;

public abstract class AbstractChangeUnitTaskDescriptor extends ReflectionTaskDescriptor {

    private final boolean isNewChangeUnit;

    public AbstractChangeUnitTaskDescriptor(String id,
                                            String order,
                                            Class<?> source,
                                            boolean runAlways,
                                            boolean transactional,
                                            boolean isNewChangeUnit) {
        super(id, order, source, runAlways, transactional);
        this.isNewChangeUnit = isNewChangeUnit;
    }

    @Override
    public Constructor<?> getConstructor() {
        if (isNewChangeUnit()) {
            try {
                return getChageUnitConstructor(FlamingockConstructor.class);
            } catch (MultipleAnnotatedConstructorsFound ex) {
                throw new FlamingockException("Found multiple constructors for class[%s] annotated with %s." +
                        " Annotate the one you want Flamingock to use to instantiate your changeUnit",
                        source.getName(),
                        FlamingockConstructor.class.getName());
            } catch (MultipleConstructorsFound ex) {
                throw new FlamingockException("Found multiple constructors without annotation %s  for class[%s].\n" +
                        "When more than one constructor, exactly one of them must be annotated. And it will be taken as default "
                        , FlamingockConstructor.class.getSimpleName()
                        , source.getName()
                );
            } catch (ConstructorNotFound ex) {
                throw new FlamingockException("Cannot find a valid constructor for class[%s]", source.getName());
            }
        } else {
            try {
                return getChageUnitConstructor(ChangeUnitConstructor.class);
            } catch (MultipleAnnotatedConstructorsFound ex) {
                throw new FlamingockException("Found multiple LEGACY constructors for class[%s] annotated with %s." +
                        " Annotate the one you want Flamingock to use to instantiate your changeUnit.\n" +
                        "Note: It's highly recommended to use the new API(@FlamingockConstructor)",
                        source.getName(),
                        ChangeUnitConstructor.class.getName());
            } catch (MultipleConstructorsFound ex) {
                throw new FlamingockException("Found multiple constructors without annotation %s  for class[%s].\n" +
                        "When more than one constructor, exactly one of them must be annotated. And it will be taken as default\n" +
                        "Note: It's highly recommended to use the new API(@FlamingockConstructor)"
                        , ChangeUnitConstructor.class.getSimpleName()
                        , source.getName()
                );
            } catch (ConstructorNotFound ex) {
                throw new FlamingockException("Cannot find a valid constructor for class[%s]", source.getName());
            }
        }

    }

    private Constructor<?> getChageUnitConstructor(Class<? extends Annotation> annotationClass) {
        List<Constructor<?>> annotatedConstructors = ReflectionUtil.getAnnotatedConstructors(source, annotationClass);
        if (annotatedConstructors.size() == 1) {
            return annotatedConstructors.get(0);
        } else if (annotatedConstructors.size() > 1) {
            throw new MultipleAnnotatedConstructorsFound();
        }
        Constructor<?>[] constructors = source.getConstructors();
        if (constructors.length == 0) {
            throw new ConstructorNotFound();
        }
        if (constructors.length > 1) {
            throw new MultipleConstructorsFound();
        }
        return constructors[0];
    }

    public boolean isNewChangeUnit() {
        return isNewChangeUnit;
    }


    private static class ConstructorNotFound extends RuntimeException {
    }

    private static class MultipleAnnotatedConstructorsFound extends RuntimeException {
    }

    private static class MultipleConstructorsFound extends RuntimeException {
    }
}
