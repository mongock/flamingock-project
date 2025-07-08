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

package io.flamingock.api.annotations;

public @interface SystemStage {
    /**
     * Specifies the location where system-level change units are found.
     * 
     * <p>The location format determines how it's interpreted:
     * <ul>
     *   <li><b>Package name:</b> Contains dots and no slashes (e.g., "com.example.system") - 
     *       Used to scan for annotated change units in the specified package</li>
     *   <li><b>Resource directory (relative):</b> Starts with "resources/" (e.g., "resources/system/migrations") - 
     *       Used to scan for template-based change units in the specified resources directory</li>
     *   <li><b>Resource directory (absolute):</b> Starts with "/" (e.g., "/absolute/path/to/system/templates") - 
     *       Used to scan for template-based change units in the specified absolute path</li>
     * </ul>
     * 
     * @return the location where system-level change units are found
     */
    String location() default "";
}