package io.flamingock.template.mongodb.model;

import java.util.Arrays;

public enum MongoOperationType {

    CREATE_COLLECTION("create-collection"),
    CREATE_INDEX("create-index");

    private final String value;

    MongoOperationType(String value) {
        this.value = value;
    }

    public static MongoOperationType getFromValue(String typeValue) {
        return Arrays.stream(MongoOperationType.values())
                .filter(type -> type.matches(typeValue))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("MongoOperation not supported: " + typeValue));
    }

    private boolean matches(String operationType) {
        return this.value.equals(operationType);
    }
}
