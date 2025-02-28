package io.flamingock.template.mongodb.model;

import com.mongodb.client.MongoDatabase;
import io.flamingock.template.mongodb.model.operator.CreateCollectionOperator;
import io.flamingock.template.mongodb.model.operator.CreateIndexOperator;
import io.flamingock.template.mongodb.model.operator.MongoOperator;

public final class MongoOperatorFactory {

    private MongoOperatorFactory(MongoDatabase mongoDatabase) {
    }

    public static MongoOperator getOperator(MongoDatabase mongoDatabase, MongoOperation op) {
        MongoOperationType typeEnum = op.getTypeEnum();
        switch (typeEnum) {
            case CREATE_COLLECTION:
                return new CreateCollectionOperator(mongoDatabase, op);
            case CREATE_INDEX:
                return new CreateIndexOperator(mongoDatabase, op);
            default:
                throw new IllegalArgumentException("MongoOperation not supported: " + op.getType());
        }

    }
}
