package io.mongock.core.mongodb;

public interface Collectionck<DOCUMENT extends Documentck> {

    String getCollectionName();

    Iterable<Documentck> listIndexes();

    String createUniqueIndex(DOCUMENT uniqueIndexDocument);

    void dropIndex(String name);

    void deleteMany(DOCUMENT document);
}
