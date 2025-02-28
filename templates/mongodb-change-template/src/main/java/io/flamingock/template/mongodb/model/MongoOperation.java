package io.flamingock.template.mongodb.model;

import com.mongodb.client.model.IndexOptions;
import io.flamingock.core.api.annotations.NonLockGuarded;
import io.flamingock.core.api.annotations.NonLockGuardedType;
import io.flamingock.template.mongodb.utils.IndexOptionsMapper;
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

    public MongoOperationType getTypeEnum() {
        return MongoOperationType.getFromValue(getType());
    }

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

    @SuppressWarnings("unchecked")
    public Document getKeys() {
        return new Document((Map<String, Object>) parameters.get("keys"));
    }

    @SuppressWarnings("unchecked")
    public IndexOptions getIndexOptions() {
        return parameters.containsKey("indexOptions")
                ? IndexOptionsMapper.mapToIndexOptions((Map<String, Object>)parameters.get("indexOptions"))
                : new IndexOptions();
    }


    @SuppressWarnings("unchecked")
    public Document getOptions() {
        return parameters.containsKey("options")
                ? new Document((Map<String, Object>) parameters.get("options"))
                : new Document();
    }

    @SuppressWarnings("unchecked")
    public Document getFilter() {
        return new Document((Map<String, Object>) parameters.get("filter"));
    }
}