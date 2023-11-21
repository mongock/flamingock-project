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

package io.flamingock.oss.driver.mongodb.v3;

import io.flamingock.oss.driver.common.mongodb.MongoDBDriverConfiguration;
import io.flamingock.oss.driver.mongodb.v3.internal.mongodb.ReadWriteConfiguration;

public class MongoDB3Configuration extends MongoDBDriverConfiguration {

    public static MongoDB3Configuration getDefault() {
        return new MongoDB3Configuration(ReadWriteConfiguration.getDefault());
    }

    private ReadWriteConfiguration readWriteConfiguration;

    public MongoDB3Configuration() {
    }

    public MongoDB3Configuration(ReadWriteConfiguration readWriteConfiguration) {
        this.readWriteConfiguration = readWriteConfiguration;
    }

    public ReadWriteConfiguration getReadWriteConfiguration() {
        return readWriteConfiguration;
    }

    public void setReadWriteConfiguration(ReadWriteConfiguration readWriteConfiguration) {
        this.readWriteConfiguration = readWriteConfiguration;
    }
}