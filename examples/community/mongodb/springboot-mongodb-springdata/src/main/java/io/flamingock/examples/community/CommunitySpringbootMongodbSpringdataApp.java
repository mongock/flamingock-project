package io.flamingock.examples.community;

import io.flamingock.community.runner.springboot.v2.EnableFlamingock;
import io.flamingock.examples.community.client.ClientRepository;
import io.flamingock.examples.community.events.PipelineFailedListener;
import io.flamingock.examples.community.events.PipelineStartedListener;
import io.flamingock.examples.community.events.PipelineCompletedListener;

import io.flamingock.examples.community.events.StageCompletedListener;
import io.flamingock.examples.community.events.StageFailedListener;
import io.flamingock.examples.community.events.StageStartedListener;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;


@EnableFlamingock
@SpringBootApplication
@EnableMongoRepositories(basePackageClasses = ClientRepository.class)
public class CommunitySpringbootMongodbSpringdataApp {

    public final static String DATABASE_NAME = "test";
    public final static String CLIENTS_COLLECTION_NAME = "clientCollection";

    public static void main(String[] args) {
        SpringApplication.run(CommunitySpringbootMongodbSpringdataApp.class, args);
    }

    @Bean
    public PipelineStartedListener startFlamingockListener() {
        return new PipelineStartedListener();
    }

    @Bean
    public PipelineCompletedListener successFlamingockListener() {
        return new PipelineCompletedListener();
    }

    @Bean
    public PipelineFailedListener sailedFlamingockListener() {
        return new PipelineFailedListener();
    }

    @Bean
    public StageStartedListener stageStartedListener() {return new StageStartedListener();}

    @Bean
    public StageCompletedListener stageCompletedListener() {return new StageCompletedListener();}

    @Bean
    public StageFailedListener stageFailedListener() {return new StageFailedListener();}

}