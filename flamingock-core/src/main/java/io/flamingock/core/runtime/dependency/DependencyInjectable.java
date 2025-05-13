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

package io.flamingock.core.runtime.dependency;

import java.io.File;
import java.net.InetAddress;
import java.net.URI;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Currency;
import java.util.Locale;
import java.util.UUID;

public interface DependencyInjectable {

    default void addDependencies(Collection<? extends Dependency> dependencies) {
        dependencies.forEach(this::addDependency);
    }

    default void addDependency(Object object) {
        addDependency(new Dependency(object));
    }

    void addDependency(Dependency dependency);

    /**
     * Idempotent removal by reference
     * @param dependency the dependency to me removed
     */
    void removeDependencyByRef(Dependency dependency);

    default void addProperty(String key, String value) {
        addDependency(new Dependency(key, String.class, value));
    }

    default void addProperty(String key, Boolean value) {
        addDependency(new Dependency(key,Boolean.class, value));
    }

    default void addProperty(String key, Integer value) {
        addDependency(new Dependency(key, Integer.class, value));
    }

    default void addProperty(String key, Float value) {
        addDependency(new Dependency(key, Float.class, value));
    }

    default void addProperty(String key, Long value) {
        addDependency(new Dependency(key, Long.class, value));
    }

    default void addProperty(String key, Double value) {
        addDependency(new Dependency(key, Double.class, value));
    }

    default void addProperty(String key, Byte value) {
        addDependency(new Dependency(key, Byte.class, value));
    }

    default void addProperty(String key, Short value) {
        addDependency(new Dependency(key, Short.class, value));
    }

    default void addProperty(String key, Character value) {
        addDependency(new Dependency(key, Character.class, value));
    }

    default void addProperty(String key, UUID value) {
        addDependency(new Dependency(key, UUID.class, value));
    }

    default void addProperty(String key, Currency value) {
        addDependency(new Dependency(key, Currency.class, value));
    }

    default void addProperty(String key, Locale value) {
        addDependency(new Dependency(key, Locale.class, value));
    }

    default void addProperty(String key, Charset value) {
        addDependency(new Dependency(key, Charset.class, value));
    }

    default void addProperty(String key, File value) {
        addDependency(new Dependency(key, File.class, value));
    }

    default void addProperty(String key, Path value) {
        addDependency(new Dependency(key, Path.class, value));
    }

    default void addProperty(String key, InetAddress value) {
        addDependency(new Dependency(key, InetAddress.class, value));
    }

    default void addProperty(String key, URL value) {
        addDependency(new Dependency(key, URL.class, value));
    }

    default void addProperty(String key, URI value) {
        addDependency(new Dependency(key, URI.class, value));
    }
}
