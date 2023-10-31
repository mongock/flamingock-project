package io.flamingock.oss.driver.couchbase;

import io.flamingock.core.configurator.LocalConfigurable;

public class CouchbaseConfiguration implements LocalConfigurable {

    public static CouchbaseConfiguration getDefault() {
        return new CouchbaseConfiguration();
    }

    private boolean indexCreation = true;

    public boolean isIndexCreation() {
        return indexCreation;
    }

    public void setIndexCreation(boolean value) {
        this.indexCreation = value;
    }
}
