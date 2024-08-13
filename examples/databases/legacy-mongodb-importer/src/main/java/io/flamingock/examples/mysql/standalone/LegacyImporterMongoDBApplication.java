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

import flamingock.internal.legacy.importer.mongodb.MongoDBLegacyImporter;
import io.flamingock.cloud.transaction.sql.SqlCloudTransactioner;
import io.flamingock.cloud.transaction.sql.SqlDialect;
import io.flamingock.core.cloud.transaction.CloudTransactioner;
import io.flamingock.core.configurator.standalone.FlamingockStandalone;
import io.flamingock.core.pipeline.Stage;
import io.flamingock.template.sql.SqlTemplateModule;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class LegacyImporterMongoDBApplication {

    private static final String SERVICE_NAME = "clients-service";
    private static final String ENVIRONMENT = "development";
    private static final String API_TOKEN = "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCIsImtpZCI6ImZsYW1pbmdvY2staW50ZXJuYWwifQ.eyJpc3MiOiJodHRwczovL2ZsYW1pbmdvY2suZXUuYXV0aDAuY29tIiwiYXVkIjoiaHR0cHM6Ly9hcGkuZmxhbWluZ29jay5pbyIsImlhdCI6MTcyMzQzMDUzMywiZXhwIjoxNzU0OTY2NTMzLCJ0b2tlbl90eXBlIjoiYXBpX3Rva2VuIiwib3JnYW5pemF0aW9uIjoiMjNjMWE1OTYtNmIwYy00Mzk0LTg3NTYtOWU0NGVhNTI3MzI4IiwicHJvamVjdCI6ImFkMjFlZTgzLTQ0ZjgtNDMyOS1iMzVhLTNiNTNiMWQyMmNmYyIsImVudmlyb25tZW50IjoiYTlmYzFjYTctM2VhMi00ODZiLTkwYTgtNjM0OGNmYmJmMTRmIiwic2VydmljZSI6ImIyOTY0NWJhLTU3MjctNDk4Yi05NmI4LWNiOGY5MzkyM2QyNyJ9.MRPQD6KLbLLMBywm9p-8uUi1DT7yVMOCFR38o42cxItA6npfEzzG4s4YR4ezXDhLb5wdpseYun1Epu4HbsiwgrpCtrMIwx-uxgcpQT7zsQCP9QoGo_4CCRbB2PxavVpGFoIZV5TXK5McTQGEoERtXJnl-1kDE7En071LP-vurXEXV-SOjO7zlA5Ct9NEyXtG3x4Tpdbxb99UBhfPwR7M2DvWbJG-p3ikYNzBkwm2-FrxwHyw7BF1UPdG--lB03ET7Hg3KcsDq69Vo-7Urdiegrkwq2xUX3C8a27i-4QhFSPbEYq5gdAaCwl-Z8NiusOLba_mROAyVdMprR3eAw";

    public static void main(String[] args) throws ClassNotFoundException {
        new LegacyImporterMongoDBApplication()
                .run();

    }

    public void run() throws ClassNotFoundException {
        try (CloudTransactioner cloudTransactioner = getSqlCloudTransactioner()) {
            FlamingockStandalone
                    .cloud()
                    .setHost("http://localhost:8080")
                    .setApiToken(API_TOKEN)
                    .setEnvironment(ENVIRONMENT)
                    .setService(SERVICE_NAME)
                    .addSystemModule(new MongoDBLegacyImporter("changeLogs"))
                    .setCloudTransactioner(cloudTransactioner)//for cloud transactions with Sql
                    .setLockAcquiredForMillis(6 * 1000L)//this is just to show how is set. Default value is still 60 * 1000L
                    .setLockQuitTryingAfterMillis(10 * 1000L)//this is just to show how is set. Default value is still 3 * 60 * 1000L
                    .setLockTryFrequencyMillis(3000L)//this is just to show how is set. Default value is still 1000L
                    .addStage(new Stage("database_stage").addFileDirectory("flamingock/stage1"))
                    .addTemplateModule(new SqlTemplateModule())
                    .addDependency(Connection.class, getConnection())
                    .build()
                    .run();
        }
    }

    private static SqlCloudTransactioner getSqlCloudTransactioner() throws ClassNotFoundException {
        Class.forName("com.mysql.cj.jdbc.Driver");
        return new SqlCloudTransactioner()
                .setDialect(SqlDialect.MYSQL)
                .setUrl("jdbc:mysql://localhost:3307/flamingock")
                .setUser("root")
                .setPassword("strong_password");
    }

    //Temporally because we haven't injected transactional = true
    private static Connection getConnection() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            return DriverManager.getConnection("jdbc:mysql://localhost:3307/flamingock", "root", "strong_password");
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

}