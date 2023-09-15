package io.flamingock.examples.community.template;

import io.flamingock.core.api.annotations.template.TemplateConfigSetter;
import io.flamingock.core.api.annotations.template.TemplateConfigValidator;
import io.flamingock.core.api.annotations.template.TemplateExecution;
import io.flamingock.core.api.annotations.template.TemplateRollbackExecution;
import org.springframework.data.mongodb.core.MongoTemplate;

public class MongoCreateCollectionTemplate {


    private MongoCreateCollectionConfiguration configuration;

    @TemplateExecution
    public void execution(MongoTemplate mongoTemplate) {
        if(!mongoTemplate.collectionExists(configuration.getCollectionName())) {
            mongoTemplate.createCollection(configuration.getCollectionName());
        }

    }

    @TemplateRollbackExecution
    public void rollback(MongoTemplate mongoTemplate) {
        if(mongoTemplate.collectionExists(configuration.getCollectionName())) {
            mongoTemplate.dropCollection(configuration.getCollectionName());
        }
    }



    @TemplateConfigSetter
    public void setConfiguration(MongoCreateCollectionConfiguration configuration) {
        this.configuration = configuration;
    }

    @TemplateConfigValidator
    public boolean validateConfiguration() {
        return configuration.getCollectionName() != null;
    }


}
