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

package io.flamingock.oss.driver.mongodb.springdata.v3.internal;

import com.mongodb.client.MongoDatabase;
import io.flamingock.commons.utils.TimeService;
import io.flamingock.oss.driver.mongodb.sync.v4.internal.MongoSync4LockService;
import io.flamingock.cloud.transaction.mongodb.sync.v4.cofig.ReadWriteConfiguration;

public class SpringDataMongoV3LockService extends MongoSync4LockService {

    protected SpringDataMongoV3LockService(MongoDatabase mongoDatabase, String lockCollectionName, ReadWriteConfiguration readWriteConfiguration) {
        super(mongoDatabase, lockCollectionName, readWriteConfiguration, TimeService.getDefault());
    }
}