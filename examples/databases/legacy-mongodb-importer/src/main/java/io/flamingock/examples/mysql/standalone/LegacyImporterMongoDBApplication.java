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

package io.flamingock.examples.mysql.standalone;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import flamingock.internal.legacy.importer.mongodb.MongoDBLegacyImporter;
import io.flamingock.core.configurator.standalone.FlamingockStandalone;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;

import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

public class LegacyImporterMongoDBApplication {

    private static final String SERVICE_NAME = "clients-service";
    private static final String ENVIRONMENT = "development";
    private static final String API_TOKEN = "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCIsImtpZCI6ImZsYW1pbmdvY2staW50ZXJuYWwifQ.eyJpc3MiOiJodHRwczovL2ZsYW1pbmdvY2suZXUuYXV0aDAuY29tIiwiYXVkIjoiaHR0cHM6Ly9hcGkuZmxhbWluZ29jay5pbyIsImlhdCI6MTcyNjQxMzQ5MiwiZXhwIjoxNzU3OTQ5NDkyLCJ0b2tlbl90eXBlIjoiYXBpX3Rva2VuIiwib3JnYW5pemF0aW9uIjoiNjAxYTZiYTktNjVhNi00YWM3LWEzMDctMzYyYTU1MTk4NjEzIiwicHJvamVjdCI6Ijc2MzgwNDdhLTlmZDUtNGI2NS1hZDc2LTU3NGFiN2U3YjRjYSIsImVudmlyb25tZW50IjoiMDJjN2UwMGItYjZkNS00NDUzLTk2MDktOGZjNjVhMWFhZWVhIiwic2VydmljZSI6IjQyOWE1ZDc5LWM3NTAtNDhkMy04MmMxLWRiYTdkY2MzMTZiNiJ9.dX5RAtvbPPvdsWAHJUX5KqPbU1iyGFP6iUiwMCS9Dms9saLofwB2eq-ayQKnpDg7aqWNTWFIV1SCjQ0avbvbx2yl94u4w1tXgPtcmSVAbHqgLCyNE4mTbpJfG_xc2qkoYWZhjGaKEnHpQy03HLgPzfmVVoYgEyJLu0K9Pd2hGot2YPtYqJ2p7MfQZv8R87NjqY_qB3co2jcY6JSfwZonxccFBRpWcA3fJ2jVtUSBd2jE6eiftyOzZWUhz2TOSt68_SU7LsoT92MrRgLc-nSmpcoj_pamTe8rWbjvu_UbQSK7WMeIpFQXEj0-y7SXS8_DlMGvf_kh2NwNUF9emA";
    private final static String MONGODB_CHANGELOG_COLLECTION = "mongockChangeLog";
    public final static String DATABASE_NAME = "test";
    public static void main(String[] args) throws ClassNotFoundException {
        new LegacyImporterMongoDBApplication()
                .run(getMongoClient("mongodb://localhost:27017/"), DATABASE_NAME);

    }

    public void run(MongoClient mongoClient, String databaseName) throws ClassNotFoundException {

        FlamingockStandalone
                    .cloud()
                    .setHost("http://localhost:8080")
                    .setApiToken(API_TOKEN)
                    .setEnvironment(ENVIRONMENT)
                    .setService(SERVICE_NAME)
                    .addSystemModule(new MongoDBLegacyImporter(MONGODB_CHANGELOG_COLLECTION,SERVICE_NAME, ENVIRONMENT, API_TOKEN))
                    .setLockAcquiredForMillis(6 * 1000L)//this is just to show how is set. Default value is still 60 * 1000L
                    .setLockQuitTryingAfterMillis(10 * 1000L)//this is just to show how is set. Default value is still 3 * 60 * 1000L
                    .setLockTryFrequencyMillis(3000L)//this is just to show how is set. Default value is still 1000L
                    .addDependency(mongoClient.getDatabase(databaseName))
                    .build()
                    .run();

    }
    private static MongoClient getMongoClient(String connectionString) {

        CodecRegistry codecRegistry = fromRegistries(CodecRegistries.fromCodecs(new ZonedDateTimeCodec()),
                MongoClientSettings.getDefaultCodecRegistry(),
                fromProviders(PojoCodecProvider.builder().automatic(true).build()));

        MongoClientSettings.Builder builder = MongoClientSettings.builder();
        builder.applyConnectionString(new ConnectionString(connectionString));
        builder.codecRegistry(codecRegistry);
        MongoClientSettings build = builder.build();
        return MongoClients.create(build);
    }

}