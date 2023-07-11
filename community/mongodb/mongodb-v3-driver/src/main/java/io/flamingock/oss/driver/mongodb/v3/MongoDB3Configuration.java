package io.flamingock.oss.driver.mongodb.v3;

import io.flamingock.community.internal.DriverConfigurable;
import io.flamingock.oss.driver.mongodb.v3.internal.mongodb.ReadWriteConfiguration;

public class MongoDB3Configuration implements DriverConfigurable {

    public static MongoDB3Configuration getDefault() {
        return new MongoDB3Configuration(ReadWriteConfiguration.getDefault());
    }

    private ReadWriteConfiguration readWriteConfiguration;

    public MongoDB3Configuration() {
    }

    public MongoDB3Configuration(ReadWriteConfiguration readWriteConfiguration) {
        this.readWriteConfiguration = readWriteConfiguration;
    }

    public ReadWriteConfiguration getReadWriteConfiguration() {
        return readWriteConfiguration;
    }

    public void setReadWriteConfiguration(ReadWriteConfiguration readWriteConfiguration) {
        this.readWriteConfiguration = readWriteConfiguration;
    }
}
