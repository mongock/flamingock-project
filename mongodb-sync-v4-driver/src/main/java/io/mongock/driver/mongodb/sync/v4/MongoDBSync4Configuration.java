package io.mongock.driver.mongodb.sync.v4;

import io.mongock.driver.mongodb.sync.v4.internal.mongodb.ReadWriteConfiguration;
import io.mongock.internal.MongockConfiguration;

public class MongoDBSync4Configuration extends MongockConfiguration {

    private ReadWriteConfiguration readWriteConfiguration;

    public ReadWriteConfiguration getReadWriteConfiguration() {
        return readWriteConfiguration;
    }

    public void setReadWriteConfiguration(ReadWriteConfiguration readWriteConfiguration) {
        this.readWriteConfiguration = readWriteConfiguration;
    }
}
