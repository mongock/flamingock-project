package io.flamingock.template.mongodb.model;

public enum MongoOperationType {

    CREATE_COLLECTION("create-collection");


    private final String value;

    MongoOperationType(String value) {
        this.value = value;
    }

    public boolean matches(String operationType) {
        return this.value.equals(operationType);
    }
}
