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

import io.flamingock.cloud.transaction.sql.SqlCloudTransactioner;
import io.flamingock.cloud.transaction.sql.SqlDialect;
import io.flamingock.core.cloud.transaction.CloudTransactioner;
import io.flamingock.core.configurator.standalone.FlamingockStandalone;
import io.flamingock.core.pipeline.Stage;
import io.flamingock.template.sql.SqlTemplateModule;

public class MysqlStandaloneApplication {

    public static void main(String[] args) throws ClassNotFoundException {
        new MysqlStandaloneApplication()
                .run();

    }


    public void run() throws ClassNotFoundException {
        try (CloudTransactioner cloudTransactioner = getSqlCloudTransactioner()) {
            FlamingockStandalone
                    .cloud()
                    .setHost("http://localhost:8080")
                    .setClientId("FAKE_CLIENT_ID")
                    .setClientSecret("FAKE_CLIENT_SECRET")
                    .setService("some_service")
                    .setCloudTransactioner(cloudTransactioner)//for cloud transactions with Sql
                    .setLockAcquiredForMillis(60 * 1000L)//this is just to show how is set. Default value is still 60 * 1000L
                    .setLockQuitTryingAfterMillis(10 * 1000L)//this is just to show how is set. Default value is still 3 * 60 * 1000L
                    .setLockTryFrequencyMillis(3000L)//this is just to show how is set. Default value is still 1000L
                    .addStage(new Stage().addFileDirectory("flamingock/stage1"))
                    .addTemplateModule(new SqlTemplateModule())
                    .build()
                    .run();
        }
    }

    private static SqlCloudTransactioner getSqlCloudTransactioner() throws ClassNotFoundException {
        Class.forName("com.mysql.cj.jdbc.Driver");
        return new SqlCloudTransactioner()
                .setDialect(SqlDialect.MYSQL)
                .setUrl("jdbc:mysql://localhost/flamingock")
                .setUser("flamingock_user")
                .setPassword("password");
    }

}