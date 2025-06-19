package io.flamingock.importer;

import io.flamingock.api.template.ChangeTemplate;
import io.flamingock.internal.common.core.error.FlamingockException;
import io.flamingock.internal.common.core.template.ChangeTemplateFactory;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Collections;

public class ImporterTemplateFactory implements ChangeTemplateFactory {

    private static final Logger logger = LoggerFactory.getLogger("ImporterAdapterFactory");

    private static final String MONGO_TEMPLATE_CLASS = "io.flamingock.importer.mongodb.MongoDbImporterChangeTemplate";
    private static final String DYNAMO_TEMPLATE_CLASS = "io.flamingock.importer.dynamodb.DynamoDbImporterChangeTemplate";
    private static final String COUCHBASE_TEMPLATE_CLASS = "io.flamingock.importer.couchbase.CouchbaseImporterChangeTemplate";


    @Override
    public Collection<ChangeTemplate<?, ?, ?>> getTemplates() {
        try {
            String className = getClassName();
            logger.info("Loading importer template: {}", className);
            Class<?> changeTemplateClass = Class.forName(className);
            return Collections.singletonList(
                    (ChangeTemplate<?, ?, ?>) changeTemplateClass.getDeclaredConstructor().newInstance()
            );

        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Importer importer template class not found" , e);
        } catch (Exception e) {
            throw new RuntimeException("Failed to instantiate importer class ", e);
        }
    }

    @NotNull
    private static String getClassName() {
        String className;

        if (isMongoDbAdapter()) {
            className = MONGO_TEMPLATE_CLASS;
        } else if (isDynamoDbAdapter()) {
            className = DYNAMO_TEMPLATE_CLASS;
        } else if (isCouchbaseAdapter()) {
            className = COUCHBASE_TEMPLATE_CLASS;
        } else {
            throw new FlamingockException("No compatible database driver detected. Please include a supported database dependency (MongoDB, DynamoDB, or Couchbase) in your project classpath.");
        }
        return className;
    }

    private static boolean isMongoDbAdapter() {
        try{
            Class.forName("com.mongodb.client.MongoCollection");
            return true;
        } catch (ClassNotFoundException e) {
            logger.warn("MongoDB adapter not found, skipping");
            return false;
        }
    }

    private static boolean isDynamoDbAdapter() {
        try{
            Class.forName("software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable");
            return true;
        } catch (ClassNotFoundException e) {
            logger.warn("DynamoDB adapter not found, skipping");
            return false;
        }
    }

    private static boolean isCouchbaseAdapter() {
        //TODO implement
        logger.warn("Couchbase adapter not implemented, skipping");
        return false;
    }
}

