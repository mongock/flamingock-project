package io.flamingock.template.mongodb.model;

import com.mongodb.client.MongoDatabase;
import io.flamingock.template.mongodb.model.MongoOperation;
import io.flamingock.template.mongodb.model.operator.CreateCollectionOperator;
import io.flamingock.template.mongodb.model.operator.MongoOperator;

import static io.flamingock.template.mongodb.model.MongoOperationType.CREATE_COLLECTION;

public final class MongoOperatorFactory {

    private MongoOperatorFactory(MongoDatabase mongoDatabase) {
    }

    public static MongoOperator getOperator(MongoDatabase mongoDatabase, MongoOperation op) {
        if (CREATE_COLLECTION.matches(op.getType())) {
            return new CreateCollectionOperator(mongoDatabase, op.getCollection());
        } else {
            throw new IllegalArgumentException("MongoOperation not supported: " + op.getType());
        }
    }
}
