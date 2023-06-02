package io.flamingock.oss.driver.common.mongodb;

public interface CollectionWrapper<DOCUMENT extends DocumentWrapper> {

    String getCollectionName();

    Iterable<DocumentWrapper> listIndexes();

    String createUniqueIndex(DOCUMENT uniqueIndexDocument);

    void dropIndex(String name);

    void deleteMany(DOCUMENT document);
}
