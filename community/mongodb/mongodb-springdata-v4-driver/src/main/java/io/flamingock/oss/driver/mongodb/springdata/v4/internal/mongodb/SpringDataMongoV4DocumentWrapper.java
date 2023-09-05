package io.flamingock.oss.driver.mongodb.springdata.v4.internal.mongodb;

import io.flamingock.oss.driver.common.mongodb.DocumentWrapper;
import org.bson.Document;

public class SpringDataMongoV4DocumentWrapper implements DocumentWrapper {

    private final Document document;

    public SpringDataMongoV4DocumentWrapper(Document document) {
        this.document = document;
    }

    public Document getDocument() {
        return document;
    }

    @Override
    public DocumentWrapper append(String key, Object value) {
        document.append(key, value);
        return this;
    }

    @Override
    public Object get(String key) {
        return document.get(key);
    }

    @Override
    public String getString(String key) {
        return document.getString(key);
    }

    @Override
    public boolean containsKey(String key) {
        return document.containsKey(key);
    }

    @Override
    public Boolean getBoolean(String key) {
        return document.getBoolean(key);
    }

    public boolean getBoolean(Object key, boolean defaultValue) {
        return document.getBoolean(key, defaultValue);
    }

    @Override
    public int size() {
        return document.size();
    }

    @Override
    public DocumentWrapper getWithWrapper(String key) {
        return new SpringDataMongoV4DocumentWrapper((Document) get(key));
    }
}
