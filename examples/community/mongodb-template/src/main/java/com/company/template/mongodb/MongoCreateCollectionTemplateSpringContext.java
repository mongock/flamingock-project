package com.company.template.mongodb;


import io.flamingock.community.runner.springboot.v2.CommunitySpringboot;
import io.flamingock.community.runner.springboot.v2.CommunitySpringbootBuilder;
import io.flamingock.core.configurator.CoreConfigurator;
import io.flamingock.core.springboot.v2.SpringRunnerBuilder;
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
