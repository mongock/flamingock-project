package io.flamingock.oss.driver.couchbase;

import io.flamingock.community.internal.DriverConfigurable;

public class CouchbaseConfiguration implements DriverConfigurable {

    public static CouchbaseConfiguration getDefault() {
        return new CouchbaseConfiguration();
    }
}
