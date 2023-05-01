package io.mongock.driver.mongodb.sync.v4;

import io.mongock.driver.mongodb.sync.v4.internal.mongodb.ReadWriteConfiguration;
import io.mongock.internal.driver.DriverConfiguration;

public class MongoDBSync4Configuration implements DriverConfiguration {

    public static MongoDBSync4Configuration getDefault() {
        return new MongoDBSync4Configuration(ReadWriteConfiguration.getDefault());
    }

    private ReadWriteConfiguration readWriteConfiguration;

    public MongoDBSync4Configuration() {
    }

    public MongoDBSync4Configuration(ReadWriteConfiguration readWriteConfiguration) {
        this.readWriteConfiguration = readWriteConfiguration;
    }

    public ReadWriteConfiguration getReadWriteConfiguration() {
        return readWriteConfiguration;
    }

    public void setReadWriteConfiguration(ReadWriteConfiguration readWriteConfiguration) {
        this.readWriteConfiguration = readWriteConfiguration;
    }
}
