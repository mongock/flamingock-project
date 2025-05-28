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

package io.flamingock.internal.core.config;

public class ValidationConfig {

    /**
     * Regex pattern for validating the order field in ChangeUnits.
     * The pattern matches strings like "001", "999", "0010", "9999".
     * It requires at least 3 digits with leading zeros.
     * Empty is allowed
     */
    private static String ORDER_FIELD_PATTERN = "^\\d{3,}$";

    public static String getOrderFieldPattern() {
        return ORDER_FIELD_PATTERN;
    }

    public static void setOrderFieldPattern(String pattern) {
        ORDER_FIELD_PATTERN = pattern;
    }
}