package io.mongock.core.mongodb;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class CollectionHelper<DOCUMENT extends DocumentWrapper> {
    private final static Logger logger = LoggerFactory.getLogger(CollectionHelper.class);


    private final static int INDEX_ENSURE_MAX_TRIES = 3;

    private final String[] uniqueFields;
    private final Supplier<DOCUMENT> documentCreator;
    private boolean ensuredCollectionIndex = false;

    private CollectionWrapper<DOCUMENT> collection;

    public CollectionHelper(CollectionWrapper<DOCUMENT> collection,
                            Supplier<DOCUMENT> documentCreator,
                            String[] uniqueFields) {
        this.collection = collection;
        this.documentCreator = documentCreator;
        this.uniqueFields = uniqueFields;
    }


    public synchronized void initialize() {
        if (!this.ensuredCollectionIndex) {
            ensureIndex(INDEX_ENSURE_MAX_TRIES);
            this.ensuredCollectionIndex = true;
        }
    }

    public void justValidateCollection() {
        if (!isIndexFine()) {
            throw new RuntimeException("Index creation not allowed, but not created or wrongly created for collection " + getCollectionName());
        }
    }

    private void ensureIndex(int tryCounter) {
        if (tryCounter <= 0) {
            throw new RuntimeException("Max tries " + INDEX_ENSURE_MAX_TRIES + " index  creation");
        }
        if (!isIndexFine()) {
            cleanResidualUniqueKeys();
            if (!isRequiredIndexCreated()) {
                createRequiredUniqueIndex();
            }
            ensureIndex(tryCounter - 1);
        }
    }

    protected boolean isIndexFine() {
        return getResidualKeys().isEmpty() && isRequiredIndexCreated();
    }

    protected void cleanResidualUniqueKeys() {
        logger.debug("Removing residual uniqueKeys for collection [{}]", getCollectionName());
        getResidualKeys().stream()
                .peek(index -> logger.debug("Removed residual uniqueKey [{}] for collection [{}]", index.toString(), getCollectionName()))
                .forEach(this::dropIndex);
    }

    private List<DocumentWrapper> getResidualKeys() {
        return StreamSupport.stream(listIndexes().spliterator(), false)
                .filter(this::doesNeedToBeRemoved)
                .collect(Collectors.toList());
    }

    private Iterable<DocumentWrapper> listIndexes() {
        return collection.listIndexes();
    }

    protected boolean doesNeedToBeRemoved(DocumentWrapper index) {
        return !isIdIndex(index) && isUniqueIndex(index) && !isRightIndex(index);
    }

    protected boolean isIdIndex(DocumentWrapper index) {
        return index.getDocument("key").get("_id") != null;
    }

    protected boolean isRequiredIndexCreated() {
        return StreamSupport.stream(
                        collection.listIndexes().spliterator(),
                        false)
                .anyMatch(this::isRightIndex);
    }

    protected void createRequiredUniqueIndex() {
        collection.createUniqueIndex(getIndexDocument(uniqueFields));
        logger.debug("Index in collection [{}] was recreated", getCollectionName());
    }

    protected boolean isRightIndex(DocumentWrapper index) {
        final DocumentWrapper key = index.getDocument("key");
        boolean keyContainsAllFields = Stream.of(uniqueFields).allMatch(uniqueField -> key.get(uniqueField) != null);
        boolean onlyTheseFields = key.size() == uniqueFields.length;
        return keyContainsAllFields && onlyTheseFields && isUniqueIndex(index);
    }

    protected boolean isUniqueIndex(DocumentWrapper index) {
        return index.getBoolean("unique", false);// checks it'unique
    }

    private String getCollectionName() {
        return collection.getCollectionName();
    }

    protected DOCUMENT getIndexDocument(String[] uniqueFields) {
        final DOCUMENT indexDocument = documentCreator.get();
        Stream.of(uniqueFields).forEach(field -> indexDocument.append(field, 1));
        return indexDocument;
    }

    protected void dropIndex(DocumentWrapper index) {
        collection.dropIndex(index.get("name").toString());
    }


    /**
     * Only for testing
     */
    public void deleteAll() {
        collection.deleteMany(documentCreator.get());
    }
}
