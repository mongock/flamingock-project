package io.flamingock.oss.driver.mongodb.v3.internal.mongodb;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.IndexOptions;
import io.flamingock.oss.driver.common.mongodb.CollectionWrapper;
import io.flamingock.oss.driver.common.mongodb.DocumentWrapper;
import org.bson.Document;


public class Mongo3CollectionWrapper implements CollectionWrapper<Mongo3DocumentWrapper> {

    private final MongoCollection<Document> collection;


    public Mongo3CollectionWrapper(MongoCollection<Document> collection) {
        this.collection = collection;
    }

    @Override
    public String getCollectionName() {
        return collection.getNamespace().getCollectionName();
    }

    @Override
    public Iterable<DocumentWrapper> listIndexes() {
        return collection.listIndexes().map(Mongo3DocumentWrapper::new);
    }

    @Override
    public String createUniqueIndex(Mongo3DocumentWrapper uniqueIndexDocument) {
        return collection.createIndex(uniqueIndexDocument.getDocument(), new IndexOptions().unique(true));
    }

    @Override
    public void dropIndex(String indexName) {
        collection.dropIndex(indexName);
    }

    @Override
    public void deleteMany(Mongo3DocumentWrapper documentWrapper) {
        collection.deleteMany(documentWrapper.getDocument());
    }


}
