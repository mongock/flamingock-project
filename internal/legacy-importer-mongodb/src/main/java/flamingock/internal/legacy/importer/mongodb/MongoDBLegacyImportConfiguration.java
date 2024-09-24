package flamingock.internal.legacy.importer.mongodb;

import io.flamingock.commons.utils.id.EnvironmentId;
import io.flamingock.commons.utils.id.ServiceId;

public class MongoDBLegacyImportConfiguration {

    private final String changeUnitsCollection;
    private final EnvironmentId environmentId;
    private final ServiceId serviceId;
    private final String serviceName;
    private final String environmentName;
    private final String apiToken;

    public MongoDBLegacyImportConfiguration(EnvironmentId environmentId,
                                            ServiceId serviceId,
                                            String changeUnitsCollection,
                                            String serviceName,
                                            String environmentName,
                                            String apiToken) {
        this.environmentId = environmentId;
        this.serviceId = serviceId;
        this.changeUnitsCollection = changeUnitsCollection;
        this.serviceName = serviceName;
        this.environmentName = environmentName;
        this.apiToken = apiToken;
    }

    public String getChangeUnitsCollection() {
        return changeUnitsCollection;
    }

    public EnvironmentId getEnvironmentId() {
        return environmentId;
    }

    public ServiceId getServiceId() {
        return serviceId;
    }
    public String getServiceName() {
        return serviceName;
    }
    public String getEnvironmentName() {
        return environmentName;
    }
    public String getApiToken() {
        return apiToken;
    }


}
