package flamingock.internal.legacy.importer.mongodb;

import flamingock.core.api.CloudSystemModule;
import flamingock.core.api.Dependency;
import flamingock.core.api.SystemModule;
import flamingock.internal.legacy.importer.mongodb.changes.MongockLegacyImporterChangeUnit;
import io.flamingock.commons.utils.id.EnvironmentId;
import io.flamingock.commons.utils.id.ServiceId;

import java.util.Collections;
import java.util.List;

public class MongoDBLegacyImporter extends CloudSystemModule {
    public static final List<Class<?>> TASK_CLASSES = Collections.singletonList(
            MongockLegacyImporterChangeUnit.class
    );

    private Iterable<Dependency> dependencies;

    private final String changeUnitsCollection;

    public MongoDBLegacyImporter(String changeUnitsCollection) {
        super(TASK_CLASSES);
        this.changeUnitsCollection = changeUnitsCollection;
    }

    @Override
    public void initialise(EnvironmentId environmentId, ServiceId serviceId) {
        MongoDBLegacyImportConfiguration configuration = new MongoDBLegacyImportConfiguration(
                environmentId, serviceId, changeUnitsCollection
        );
        dependencies = Collections.singletonList(
                new Dependency(MongoDBLegacyImportConfiguration.class, configuration)
        );
    }

    @Override
    public Iterable<Dependency> getDependencies() {
        return dependencies;
    }
}
