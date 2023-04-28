package io.mongock.driver.mongodb.sync.v4.internal.mongodb;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.IndexOptions;
import io.mongock.core.mongodb.Collectionck;
import io.mongock.core.mongodb.Documentck;
import org.bson.Document;


public class CollectionckImpl implements Collectionck<DocumentckImpl> {

    private final MongoCollection<Document> collection;


    public CollectionckImpl(MongoCollection<Document> collection) {
        this.collection = collection;
    }

    @Override
    public String getCollectionName() {
        return collection.getNamespace().getCollectionName();
    }

    @Override
    public Iterable<Documentck> listIndexes() {
        return collection.listIndexes().map(DocumentckImpl::new);
    }

    @Override
    public String createUniqueIndex(DocumentckImpl uniqueIndexDocument) {
        return collection.createIndex(uniqueIndexDocument.getDocument(), new IndexOptions().unique(true));
    }

    @Override
    public void dropIndex(String indexName) {
        collection.dropIndex(indexName);
    }

    @Override
    public void deleteMany(DocumentckImpl documentck) {
        collection.deleteMany(documentck.getDocument());
    }


}
