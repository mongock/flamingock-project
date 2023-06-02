package io.flamingock.oss.driver.mongodb.sync.v4.internal.mongodb;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.IndexOptions;
import io.flamingock.oss.driver.common.mongodb.CollectionWrapper;
import io.flamingock.oss.driver.common.mongodb.DocumentWrapper;
import org.bson.Document;


public class MongoSync4CollectionWrapper implements CollectionWrapper<MongoSync4DocumentWrapper> {

    private final MongoCollection<Document> collection;


    public MongoSync4CollectionWrapper(MongoCollection<Document> collection) {
        this.collection = collection;
    }

    @Override
    public String getCollectionName() {
        return collection.getNamespace().getCollectionName();
    }

    @Override
    public Iterable<DocumentWrapper> listIndexes() {
        return collection.listIndexes().map(MongoSync4DocumentWrapper::new);
    }

    @Override
    public String createUniqueIndex(MongoSync4DocumentWrapper uniqueIndexDocument) {
        return collection.createIndex(uniqueIndexDocument.getDocument(), new IndexOptions().unique(true));
    }

    @Override
    public void dropIndex(String indexName) {
        collection.dropIndex(indexName);
    }

    @Override
    public void deleteMany(MongoSync4DocumentWrapper documentck) {
        collection.deleteMany(documentck.getDocument());
    }


}
