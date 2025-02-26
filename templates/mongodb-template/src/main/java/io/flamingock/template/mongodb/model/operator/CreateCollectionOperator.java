package io.flamingock.template.mongodb.model.operator;

import com.mongodb.client.MongoDatabase;

public class CreateCollectionOperator extends MongoOperator {

    private final String collectionName;

    public CreateCollectionOperator(MongoDatabase mongoDatabase, String collectionName) {
        super(mongoDatabase);
        this.collectionName = collectionName;
    }

    @Override
    public void execute() {
        mongoDatabase.createCollection(collectionName);
    }
}
