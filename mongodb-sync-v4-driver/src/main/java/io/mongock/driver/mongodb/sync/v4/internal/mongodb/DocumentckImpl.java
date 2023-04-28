package io.mongock.driver.mongodb.sync.v4.internal.mongodb;

import io.mongock.core.mongodb.Documentck;
import org.bson.Document;

public class DocumentckImpl implements Documentck {

    private final Document document;

    public DocumentckImpl(Document document) {
        this.document = document;
    }

    public Document getDocument() {
        return document;
    }

    @Override
    public Documentck append(String key, Object value) {
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
    public Documentck getDocument(String key) {
        Document retrievedDocument = (Document) document.get(key);
        return new DocumentckImpl(retrievedDocument);
    }
}
