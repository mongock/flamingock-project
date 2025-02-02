package io.flamingock.importer.cloud.mongodb.v4;

import com.mongodb.client.MongoCollection;
import io.flamingock.commons.utils.id.EnvironmentId;
import io.flamingock.commons.utils.id.ServiceId;
import io.flamingock.core.api.CloudSystemModule;
import io.flamingock.core.runtime.dependency.Dependency;
import org.bson.Document;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class MongoDBLegacyImporter implements CloudSystemModule {
    public static final List<Class<?>> TASK_CLASSES = Collections.singletonList(
            MongockLegacyImporterChangeUnit.class
    );
    private final MongoCollection<Document> changeUnitsCollection;
    private List<Dependency> dependencies;

    public MongoDBLegacyImporter(MongoCollection<Document> changeUnitsCollection) {
        this.changeUnitsCollection = changeUnitsCollection;
    }

    @Override
    public void initialise(EnvironmentId environmentId, ServiceId serviceId, String jwt, String serverHost) {
        dependencies = Collections.singletonList(
                new Dependency(
                        MongoDBLegacyImportConfiguration.class,
                        new MongoDBLegacyImportConfiguration(
                                environmentId, serviceId, jwt, serverHost, changeUnitsCollection
                        )
                )
        );
    }

    @Override
    public int getOrder() {
        return 0;
    }

    @Override
    public String getName() {
        return "mongodb-legacy-importer";
    }

    @Override
    public Collection<Class<?>> getTaskClasses() {
        return TASK_CLASSES;
    }

    @Override
    public List<Dependency> getDependencies() {
        return dependencies;
    }

    @Override
    public boolean isBeforeUserStages() {
        return true;
    }
}
