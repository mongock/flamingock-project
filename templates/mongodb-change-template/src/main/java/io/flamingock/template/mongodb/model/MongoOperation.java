package io.flamingock.template.mongodb.model;

import io.flamingock.core.api.annotations.NonLockGuarded;
import io.flamingock.core.api.annotations.NonLockGuardedType;
import org.bson.Document;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@NonLockGuarded(NonLockGuardedType.NONE)
public class MongoOperation {
    private String type;
    private String collection;
    private Map<String, Object> parameters;

    public String getType() { return type; }
    public String getCollection() { return collection; }
    public Map<String, Object> getParameters() { return parameters; }

    public void setType(String type) { this.type = type; }
    public void setCollection(String collection) { this.collection = collection; }
    public void setParameters(Map<String, Object> parameters) { this.parameters = parameters; }

    @SuppressWarnings("unchecked")
    public List<Document> getDocuments() {
        return ((List<Map<String, Object>>) parameters.get("documents"))
                .stream().map(Document::new)
                .collect(Collectors.toList());
    }

    public Document getKeys() {
        return new Document((Map<String, Object>) parameters.get("keys"));
    }

    public Document getOptions() {
        return parameters.containsKey("options")
                ? new Document((Map<String, Object>) parameters.get("options"))
                : new Document();
    }

    public Document getFilter() {
        return new Document((Map<String, Object>) parameters.get("filter"));
    }
}