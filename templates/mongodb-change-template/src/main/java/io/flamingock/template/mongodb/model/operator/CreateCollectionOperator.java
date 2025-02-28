package io.flamingock.template.mongodb.model.operator;

import com.mongodb.client.MongoDatabase;
import io.flamingock.template.mongodb.model.MongoOperation;

public class CreateCollectionOperator extends MongoOperator {

    public CreateCollectionOperator(MongoDatabase mongoDatabase, MongoOperation operation) {
        super(mongoDatabase, operation);
    }

    @Override
    public void execute() {
        mongoDatabase.createCollection(op.getCollection());
    }
}
