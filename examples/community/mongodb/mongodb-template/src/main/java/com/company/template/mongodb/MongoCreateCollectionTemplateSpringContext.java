package com.company.template.mongodb;


import io.flamingock.core.configurator.CoreConfigurator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MongoCreateCollectionTemplateSpringContext {
    
    @Bean
    public MongoCreateCollectionTemplateModule mongoCreateCollectionTemplateModule(CoreConfigurator<?> CoreConfigurator) {
        MongoCreateCollectionTemplateModule module = new MongoCreateCollectionTemplateModule();
        CoreConfigurator.addTemplateModule(module);
        return module;
    }
}
