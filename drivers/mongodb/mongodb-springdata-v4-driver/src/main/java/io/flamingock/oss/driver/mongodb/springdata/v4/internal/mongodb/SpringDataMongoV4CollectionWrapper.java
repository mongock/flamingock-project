package io.flamingock.oss.driver.mongodb.springdata.v4.internal.mongodb;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.IndexOptions;
import io.flamingock.oss.driver.common.mongodb.CollectionWrapper;
import io.flamingock.oss.driver.common.mongodb.DocumentWrapper;
import org.bson.Document;

public class SpringDataMongoV4CollectionWrapper implements CollectionWrapper<SpringDataMongoV4DocumentWrapper> {

    private final MongoCollection<Document> collection;


    public SpringDataMongoV4CollectionWrapper(MongoCollection<Document> collection) {
        this.collection = collection;
    }

    @Override
    public String getCollectionName() {
        return collection.getNamespace().getCollectionName();
    }

    @Override
    public Iterable<DocumentWrapper> listIndexes() {
        return collection.listIndexes().map(SpringDataMongoV4DocumentWrapper::new);
    }

    @Override
    public String createUniqueIndex(SpringDataMongoV4DocumentWrapper uniqueIndexDocument) {
        return collection.createIndex(uniqueIndexDocument.getDocument(), new IndexOptions().unique(true));
    }

    @Override
    public void dropIndex(String indexName) {
        collection.dropIndex(indexName);
    }

    @Override
    public void deleteMany(SpringDataMongoV4DocumentWrapper documentWrapper) {
        collection.deleteMany(documentWrapper.getDocument());
    }
}
