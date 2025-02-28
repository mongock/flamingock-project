package io.flamingock.template.mongodb.model.operator;

import com.mongodb.client.MongoDatabase;
import io.flamingock.template.mongodb.model.MongoOperation;

public class CreateIndexOperator extends MongoOperator {


    public CreateIndexOperator(MongoDatabase mongoDatabase, MongoOperation operation) {
        super(mongoDatabase, operation);
    }

    @Override
    public void execute() {
        mongoDatabase.getCollection(op.getCollection())
                .createIndex(
                        op.getKeys(),
                        op.getIndexOptions()
                );
    }
}
