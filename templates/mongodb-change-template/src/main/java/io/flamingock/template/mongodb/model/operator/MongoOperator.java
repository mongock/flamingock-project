package io.flamingock.template.mongodb.model.operator;

import com.mongodb.client.MongoDatabase;

public abstract class MongoOperator {
    protected final MongoDatabase mongoDatabase;

    protected MongoOperator(MongoDatabase mongoDatabase) {
        this.mongoDatabase = mongoDatabase;
    }

    public abstract void execute();
}
