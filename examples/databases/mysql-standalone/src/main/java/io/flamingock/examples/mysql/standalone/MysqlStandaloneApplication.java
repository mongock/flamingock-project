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
import io.flamingock.core.configurator.standalone.FlamingockStandalone;
import io.flamingock.core.pipeline.Stage;

import io.flamingock.oss.driver.mongodb.sync.v4.driver.MongoSync4Driver;
import io.flamingock.template.sql.SqlTemplateModule;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

public class MysqlStandaloneApplication {


    public final static String DATABASE_NAME = "test";
    public static void main(String[] args) throws SQLException, ClassNotFoundException {
        new MysqlStandaloneApplication()
                .run(getMongoClient("mongodb://localhost:27017/"), DATABASE_NAME);

    }


    public  void run(MongoClient mongoClient, String databaseName) throws SQLException, ClassNotFoundException {
        FlamingockStandalone
                .local()
                .setDriver(new MongoSync4Driver(mongoClient, databaseName))
                .setLockAcquiredForMillis(60 * 1000L)//this is just to show how is set. Default value is still 60 * 1000L
                .setLockQuitTryingAfterMillis(3 * 60 * 1000L)//this is just to show how is set. Default value is still 3 * 60 * 1000L
                .setLockTryFrequencyMillis(1000L)//this is just to show how is set. Default value is still 1000L
                .addStage(new Stage().addFileDirectory("flamingock/stage1"))
                .addDependency(mongoClient.getDatabase(databaseName))
                .addDependency(mysqlConnection())
                .addTemplateModule(new SqlTemplateModule())
                .build()
                .run();
    }

    private static Connection mysqlConnection() throws ClassNotFoundException, SQLException {
        String myDriver = "com.mysql.cj.jdbc.Driver";
        String myUrl = "jdbc:mysql://localhost/flamingock";
        Class.forName(myDriver);
        return DriverManager.getConnection(myUrl, "flamingock_user", "password");
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