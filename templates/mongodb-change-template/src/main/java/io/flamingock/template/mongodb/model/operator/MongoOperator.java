package io.flamingock.template.mongodb.model.operator;

import com.mongodb.client.MongoDatabase;
import io.flamingock.template.mongodb.model.MongoOperation;

public abstract class MongoOperator {
    protected final MongoDatabase mongoDatabase;
    protected final MongoOperation op;

    protected MongoOperator(MongoDatabase mongoDatabase, MongoOperation op) {
        this.mongoDatabase = mongoDatabase;
        this.op = op;
    }

    public abstract void execute();
}
