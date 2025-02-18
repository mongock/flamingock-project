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

package io.flamingock.importer.cloud.mongodb.v4.legacy;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import io.flamingock.commons.utils.id.EnvironmentId;
import io.flamingock.commons.utils.id.ServiceId;
import io.flamingock.core.runtime.dependency.Dependency;
import io.flamingock.importer.cloud.common.Importer;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;


public class MongoDBLegacyImporter implements Importer {

    private final MongoCollection<Document> changeUnitsCollection;
    private List<Dependency> dependencies;

    public MongoDBLegacyImporter(MongoClient mongoClient, String dbName) {
        this.changeUnitsCollection = mongoClient.getDatabase(dbName).getCollection(DEFAULT_MONGOCK_REPOSITORY_NAME);
    }

    public MongoDBLegacyImporter(MongoClient mongoClient, String dbName, String overridenChangelogCollection) {
        this.changeUnitsCollection = mongoClient.getDatabase(dbName).getCollection(overridenChangelogCollection);
    }

    @Override
    public void initialise(EnvironmentId environmentId, ServiceId serviceId, String jwt, String serverHost) {
        dependencies = new ArrayList<>();
        dependencies.add(
                new Dependency(
                        MongoDBLegacyImportConfiguration.class,
                        new MongoDBLegacyImportConfiguration(
                                environmentId, serviceId, jwt, serverHost
                        )
                )
        );
        dependencies.add(
                new Dependency(
                        MongoDBLegacyAuditReader.class,
                        new MongoDBLegacyAuditReader(changeUnitsCollection)
                )
        );
    }

    @Override
    public List<Dependency> getDependencies() {
        return dependencies;
    }

}
