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

package io.flamingock.community.mongodb.sync.changes;

import com.mongodb.client.MongoDatabase;
import io.flamingock.api.annotations.ChangeUnit;
import io.flamingock.api.annotations.Execution;

@ChangeUnit( id="create-client-collection" , order = "001", transactional = false)
public class _001_create_client_collection_happy {

    @Execution
    public void execution(MongoDatabase mongoDatabase) {
        mongoDatabase.createCollection("clientCollection");
    }
}
