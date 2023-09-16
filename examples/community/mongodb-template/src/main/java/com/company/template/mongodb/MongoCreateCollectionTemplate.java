package com.company.template.mongodb;

import io.flamingock.template.annotations.TemplateConfigSetter;
import io.flamingock.template.annotations.TemplateConfigValidator;
import io.flamingock.template.annotations.TemplateExecution;
import io.flamingock.template.annotations.TemplateRollbackExecution;
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
