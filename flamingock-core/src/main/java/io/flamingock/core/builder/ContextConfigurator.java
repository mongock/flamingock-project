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

package io.flamingock.core.builder;


import io.flamingock.core.event.model.IPipelineCompletedEvent;
import io.flamingock.core.event.model.IPipelineFailedEvent;
import io.flamingock.core.event.model.IPipelineIgnoredEvent;
import io.flamingock.core.event.model.IPipelineStartedEvent;
import io.flamingock.core.event.model.IStageCompletedEvent;
import io.flamingock.core.event.model.IStageFailedEvent;
import io.flamingock.core.event.model.IStageIgnoredEvent;
import io.flamingock.core.event.model.IStageStartedEvent;
import io.flamingock.core.runtime.dependency.Dependency;

import java.io.File;
import java.net.InetAddress;
import java.net.URI;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.time.Period;
import java.time.ZonedDateTime;
import java.util.Currency;
import java.util.Locale;
import java.util.UUID;
import java.util.function.Consumer;

public interface ContextConfigurator<HOLDER> {


    /**
     * Manually adds a dependency to be used in the  changeUnits, which can be retrieved by its own type
     *
     * @param instance dependency
     * @return builder for fluent interface
     */
    HOLDER addDependency(Object instance);

    /**
     * Manually adds a dependency to be used in the  changeUnits, which can be retrieved by a name
     *
     * @param name     name for which it should be searched by
     * @param instance dependency
     * @return builder for fluent interface
     */
    HOLDER addDependency(String name, Object instance);

    /**
     * Manually adds a dependency to be used in the  changeUnits, which can be retrieved by a type
     *
     * @param type     type for which it should be searched by
     * @param instance dependency
     * @return builder for fluent interface
     */
    HOLDER addDependency(Class<?> type, Object instance);

    /**
     * Manually adds a dependency to be used in the  changeUnits, which can be retrieved by a type or name
     *
     * @param name     name for which it should be searched by
     * @param type     type for which it should be searched by
     * @param instance dependency
     * @return builder for fluent interface
     */
    HOLDER addDependency(String name, Class<?> type, Object instance);

    //TODO javadoc
    HOLDER setPipelineStartedListener(Consumer<IPipelineStartedEvent> listener);

    //TODO javadoc
    HOLDER setPipelineCompletedListener(Consumer<IPipelineCompletedEvent> listener);

    //TODO javadoc
    HOLDER setPipelineIgnoredListener(Consumer<IPipelineIgnoredEvent> listener);

    //TODO javadoc
    HOLDER setPipelineFailedListener(Consumer<IPipelineFailedEvent> listener);

    //TODO javadoc
    HOLDER setStageStartedListener(Consumer<IStageStartedEvent> listener);

    //TODO javadoc
    HOLDER setStageCompletedListener(Consumer<IStageCompletedEvent> listener);

    //TODO javadoc
    HOLDER setStageIgnoredListener(Consumer<IStageIgnoredEvent> listener);

    //TODO javadoc
    HOLDER setStageFailedListener(Consumer<IStageFailedEvent> listener);

    Consumer<IPipelineStartedEvent> getPipelineStartedListener();

    //TODO javadoc
    Consumer<IPipelineCompletedEvent> getPipelineCompletedListener();

    //TODO javadoc
    Consumer<IPipelineIgnoredEvent> getPipelineIgnoredListener();

    //TODO javadoc
    Consumer<IPipelineFailedEvent> getPipelineFailureListener();

    //TODO javadoc
    Consumer<IStageStartedEvent> getStageStartedListener();

    //TODO javadoc
    Consumer<IStageCompletedEvent> getStageCompletedListener();

    //TODO javadoc
    Consumer<IStageIgnoredEvent> getStageIgnoredListener();

    //TODO javadoc
    Consumer<IStageFailedEvent> getStageFailureListener();


    default HOLDER addProperty(String key, String value) {
        return addDependency(new Dependency(key, String.class, value));
    }

    default HOLDER addProperty(String key, Boolean value) {
        return addDependency(new Dependency(key,Boolean.class, value));
    }

    default HOLDER addProperty(String key, Integer value) {
        return addDependency(new Dependency(key, Integer.class, value));
    }

    default HOLDER addProperty(String key, Float value) {
        return addDependency(new Dependency(key, Float.class, value));
    }

    default HOLDER addProperty(String key, Long value) {
        return addDependency(new Dependency(key, Long.class, value));
    }

    default HOLDER addProperty(String key, Double value) {
        return addDependency(new Dependency(key, Double.class, value));
    }

    default HOLDER addProperty(String key, UUID value) {
        return addDependency(new Dependency(key, UUID.class, value));
    }

    default HOLDER addProperty(String key, Currency value) {
        return addDependency(new Dependency(key, Currency.class, value));
    }

    default HOLDER addProperty(String key, Locale value) {
        return addDependency(new Dependency(key, Locale.class, value));
    }

    default HOLDER addProperty(String key, Charset value) {
        return addDependency(new Dependency(key, Charset.class, value));
    }

    default HOLDER addProperty(String key, File value) {
        return addDependency(new Dependency(key, File.class, value));
    }

    default HOLDER addProperty(String key, Path value) {
        return addDependency(new Dependency(key, Path.class, value));
    }

    default HOLDER addProperty(String key, InetAddress value) {
        return addDependency(new Dependency(key, InetAddress.class, value));
    }

    default HOLDER addProperty(String key, URL value) {
        return addDependency(new Dependency(key, URL.class, value));
    }

    default HOLDER addProperty(String key, URI value) {
        return addDependency(new Dependency(key, URI.class, value));
    }

    default HOLDER addProperty(String key, Duration value) {
        return addDependency(new Dependency(key, Duration.class, value));
    }

    default HOLDER addProperty(String key, Period value) {
        return addDependency(new Dependency(key, Period.class, value));
    }

    default HOLDER addProperty(String key, Instant value) {
        return addDependency(new Dependency(key, Instant.class, value));
    }

    default HOLDER addProperty(String key, LocalDate value) {
        return addDependency(new Dependency(key, LocalDate.class, value));
    }

    default HOLDER addProperty(String key, LocalTime value) {
        return addDependency(new Dependency(key, LocalTime.class, value));
    }

    default HOLDER addProperty(String key, LocalDateTime value) {
        return addDependency(new Dependency(key, LocalDateTime.class, value));
    }

    default HOLDER addProperty(String key, ZonedDateTime value) {
        return addDependency(new Dependency(key, ZonedDateTime.class, value));
    }

    default HOLDER addProperty(String key, OffsetDateTime value) {
        return addDependency(new Dependency(key, OffsetDateTime.class, value));
    }

    default HOLDER addProperty(String key, OffsetTime value) {
        return addDependency(new Dependency(key, OffsetTime.class, value));
    }
}
