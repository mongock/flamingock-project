package io.flamingock.examples.community;


import io.flamingock.core.springboot.v3.context.EnableFlamingock;
import io.flamingock.examples.community.client.ClientRepository;
import io.flamingock.examples.community.events.FailureEventListener;
import io.flamingock.examples.community.events.StartedEventListener;
import io.flamingock.examples.community.events.SuccessEventListener;

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
    public StartedEventListener startFlamingockListener() {
        return new StartedEventListener();
    }

    @Bean
    public SuccessEventListener successFlamingockListener() {
        return new SuccessEventListener();
    }

    @Bean
    public FailureEventListener sailedFlamingockListener() {
        return new FailureEventListener();
    }

}