package io.mongock.driver.mongodb.sync.v4;

import io.mongock.driver.mongodb.sync.v4.internal.mongodb.ReadWriteConfiguration;
import io.mongock.internal.driver.DriverConfiguration;

public class MongoDBSync4Configuration implements DriverConfiguration {

    private ReadWriteConfiguration readWriteConfiguration;

    public ReadWriteConfiguration getReadWriteConfiguration() {
        return readWriteConfiguration;
    }

    public void setReadWriteConfiguration(ReadWriteConfiguration readWriteConfiguration) {
        this.readWriteConfiguration = readWriteConfiguration;
    }
}
