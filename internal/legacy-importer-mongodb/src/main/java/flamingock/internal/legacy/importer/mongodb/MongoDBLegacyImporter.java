package flamingock.internal.legacy.importer.mongodb;

import io.flamingock.core.api.CloudSystemModule;
import io.flamingock.core.runtime.dependency.Dependency;
import io.flamingock.commons.utils.id.EnvironmentId;
import io.flamingock.commons.utils.id.ServiceId;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class MongoDBLegacyImporter implements CloudSystemModule {
    public static final List<Class<?>> TASK_CLASSES = Collections.singletonList(
            MongockLegacyImporterChangeUnit.class
    );

    private List<Dependency> dependencies;

    private final String changeUnitsCollection;
    private final String serviceName;
    private final String environmentName;
    private final String apiToken;

    public MongoDBLegacyImporter(String changeUnitsCollection, String serviceName, String environmentName, String apiToken) {
        this.changeUnitsCollection = changeUnitsCollection;
        this.serviceName = serviceName;
        this.environmentName = environmentName;
        this.apiToken = apiToken;
    }

    @Override
    public void initialise(EnvironmentId environmentId, ServiceId serviceId) {
        MongoDBLegacyImportConfiguration configuration = new MongoDBLegacyImportConfiguration(
                environmentId, serviceId, changeUnitsCollection, serviceName, environmentName, apiToken
        );
        dependencies = Collections.singletonList(
                new Dependency(MongoDBLegacyImportConfiguration.class, configuration)
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
