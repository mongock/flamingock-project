package flamingock.internal.legacy.importer.mongodb;


import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import io.changock.migration.api.annotations.NonLockGuarded;
import io.flamingock.commons.utils.JsonObjectMapper;
import io.flamingock.commons.utils.RunnerId;
import io.flamingock.commons.utils.http.Http;
import io.flamingock.core.api.annotations.ChangeUnit;
import io.flamingock.core.api.annotations.Execution;
import io.flamingock.core.api.annotations.SystemChange;
import io.flamingock.core.cloud.api.auth.AuthResponse;
import io.flamingock.core.cloud.auth.AuthClient;
import io.flamingock.core.cloud.auth.AuthManager;
import io.flamingock.core.cloud.auth.HttpAuthClient;
import org.apache.http.impl.client.HttpClients;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;

@SystemChange
@ChangeUnit(id = "mongock-legacy-importer-mongodb-v1", order = "1")
public class MongockLegacyImporterChangeUnit {

    private static final Logger logger = LoggerFactory.getLogger(MongockLegacyImporterChangeUnit.class);

    @Execution
    public void execution(@NonLockGuarded MongoDBLegacyImportConfiguration configuration, @NonLockGuarded MongoDatabase mongoDatabase) {

        //Get MongoDB ChangeLog
        MongoCollection<Document> collection = mongoDatabase.getCollection(configuration.getChangeUnitsCollection());

        ArrayList<Document> data = collection
                .find()
                .into(new ArrayList<>());

        //Instance HttpClient

        Http.RequestBuilderFactory requestBuilderFactory = Http.builderFactory(HttpClients.createDefault(), JsonObjectMapper.DEFAULT_INSTANCE);
        Http.RequestBuilder requestBuilder = requestBuilderFactory.getRequestBuilder("http://localhost:8080");

        AuthClient authClient = new HttpAuthClient(
                "http://localhost:8080",
                "v1",
                requestBuilderFactory);

        String pathTemplate = String.format(
                "/api/v1/environment/%s/service/%s/execution/import",
                configuration.getServiceId().toString(),
                configuration.getEnvironmentId().toString()
        );

        try {

            AuthManager authManager = new AuthManager(
                    configuration.getApiToken(),
                    configuration.getServiceName(),
                    configuration.getEnvironmentName(),
                    authClient);

            AuthResponse authResponse = authManager.authenticate();
            RunnerId runnerId = RunnerId.generate();

            requestBuilder
                    .POST(pathTemplate)
                    .withRunnerId(runnerId)
                    .withBearerToken(authManager.getJwtToken())
                    .setBody(data)
                    .execute();

        } catch (Throwable throwable) {
            logger.error("Error writing legacy audit:\n{}", throwable.toString());
        }
    }
}
