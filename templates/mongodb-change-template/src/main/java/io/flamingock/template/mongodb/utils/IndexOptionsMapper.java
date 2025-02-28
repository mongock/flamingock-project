package io.flamingock.template.mongodb.utils;
import com.mongodb.client.model.Collation;
import com.mongodb.client.model.IndexOptions;
import org.bson.BsonDocument;
import org.bson.BsonValue;
import org.bson.conversions.Bson;

import java.util.Map;
import java.util.concurrent.TimeUnit;

public class IndexOptionsMapper {

    public static IndexOptions mapToIndexOptions(Map<String, Object> options) {
        IndexOptions indexOptions = new IndexOptions();

        if (options.containsKey("background")) {
            indexOptions.background(getBoolean(options, "background"));
        }
        if (options.containsKey("unique")) {
            indexOptions.unique(getBoolean(options, "unique"));
        }
        if (options.containsKey("name")) {
            indexOptions.name(getString(options, "name"));
        }
        if (options.containsKey("sparse")) {
            indexOptions.sparse(getBoolean(options, "sparse"));
        }
        if (options.containsKey("expireAfterSeconds")) {
            indexOptions.expireAfter(getLong(options, "expireAfterSeconds"), TimeUnit.SECONDS);
        }
        if (options.containsKey("version")) {
            indexOptions.version(getInteger(options, "version"));
        }
        if (options.containsKey("weights")) {
            indexOptions.weights(getBson(options, "weights"));
        }
        if (options.containsKey("defaultLanguage")) {
            indexOptions.defaultLanguage(getString(options, "defaultLanguage"));
        }
        if (options.containsKey("languageOverride")) {
            indexOptions.languageOverride(getString(options, "languageOverride"));
        }
        if (options.containsKey("textVersion")) {
            indexOptions.textVersion(getInteger(options, "textVersion"));
        }
        if (options.containsKey("sphereVersion")) {
            indexOptions.sphereVersion(getInteger(options, "sphereVersion"));
        }
        if (options.containsKey("bits")) {
            indexOptions.bits(getInteger(options, "bits"));
        }
        if (options.containsKey("min")) {
            indexOptions.min(getDouble(options, "min"));
        }
        if (options.containsKey("max")) {
            indexOptions.max(getDouble(options, "max"));
        }
        if (options.containsKey("bucketSize")) {
            indexOptions.bucketSize(getDouble(options, "bucketSize"));
        }
        if (options.containsKey("storageEngine")) {
            indexOptions.storageEngine(getBson(options, "storageEngine"));
        }
        if (options.containsKey("partialFilterExpression")) {
            indexOptions.partialFilterExpression(getBson(options, "partialFilterExpression"));
        }
        if (options.containsKey("collation")) {
            indexOptions.collation(getCollation(options, "collation"));
        }
        if (options.containsKey("wildcardProjection")) {
            indexOptions.wildcardProjection(getBson(options, "wildcardProjection"));
        }
        if (options.containsKey("hidden")) {
            indexOptions.hidden(getBoolean(options, "hidden"));
        }

        return indexOptions;
    }


    // Utility methods for safe type checking with exception handling

    private static Boolean getBoolean(Map<String, Object> options, String key) {
        Object value = options.get(key);
        if (value instanceof Boolean) {
            return (Boolean) value;
        } else {
            throw new IllegalArgumentException(String.format("field[%s] should be Boolean", key));
        }
    }

    private static String getString(Map<String, Object> options, String key) {
        Object value = options.get(key);
        if (value instanceof String) {
            return (String) value;
        } else {
            throw new IllegalArgumentException(String.format("field[%s] should be String", key));
        }
    }

    private static Integer getInteger(Map<String, Object> options, String key) {
        Object value = options.get(key);
        if (value instanceof Number) {
            return ((Number) value).intValue();
        } else {
            throw new IllegalArgumentException(String.format("field[%s] should be Integer", key));
        }
    }

    private static Long getLong(Map<String, Object> options, String key) {
        Object value = options.get(key);
        if (value instanceof Number) {
            return ((Number) value).longValue();
        } else {
            throw new IllegalArgumentException(String.format("field[%s] should be Long", key));
        }
    }

    private static Double getDouble(Map<String, Object> options, String key) {
        Object value = options.get(key);
        if (value instanceof Number) {
            return ((Number) value).doubleValue();
        } else {
            throw new IllegalArgumentException(String.format("field[%s] should be Double", key));
        }
    }

    private static Bson getBson(Map<String, Object> options, String key) {
        Object value = options.get(key);
        if (value instanceof Bson) {
            return (Bson) value;
        } else if (value instanceof Map) {
            return toBsonDocument((Map<String, Object>) value);
        } else {
            throw new IllegalArgumentException(String.format("field[%s] should be Bson", key));
        }
    }

    private static Collation getCollation(Map<String, Object> options, String key) {
        Object value = options.get(key);
        if (value instanceof Collation) {
            return (Collation) value;
        } else {
            throw new IllegalArgumentException(String.format("field[%s] should be Collation", key));
        }
    }

    // Recursively converts a Map<String, Object> to BsonDocument
    private static BsonDocument toBsonDocument(Map<String, Object> map) {
        BsonDocument document = new BsonDocument();
        map.forEach((key, value) -> document.append(key, toBsonValue(value)));
        return document;
    }

    // Converts Java types into BSON types
    @SuppressWarnings("unchecked")
    private static BsonValue toBsonValue(Object value) {
        if (value instanceof String) {
            return new org.bson.BsonString((String) value);
        } else if (value instanceof Integer) {
            return new org.bson.BsonInt32((Integer) value);
        } else if (value instanceof Long) {
            return new org.bson.BsonInt64((Long) value);
        } else if (value instanceof Double) {
            return new org.bson.BsonDouble((Double) value);
        } else if (value instanceof Boolean) {
            return new org.bson.BsonBoolean((Boolean) value);
        } else if (value instanceof Map) {
            return toBsonDocument((Map<String, Object>) value);
        }
        throw new IllegalArgumentException("Unsupported BSON type: " + value.getClass().getSimpleName());
    }
}

