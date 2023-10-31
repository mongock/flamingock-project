package io.flamingock.examples.community;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoDatabase;

import io.flamingock.core.driver.ConnectionDriver;
import io.flamingock.community.runner.springboot.v2.EnableFlamingock;
import io.flamingock.examples.community.events.FailureEventListener;
import io.flamingock.examples.community.events.StartedEventListener;
import io.flamingock.examples.community.events.SuccessEventListener;
import io.flamingock.oss.driver.mongodb.sync.v4.driver.MongoSync4Driver;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;


@EnableFlamingock
@SpringBootApplication
public class CommunitySpringbootMongodbSyncApp {
    public final static String DATABASE_NAME = "test";

    public final static String CLIENTS_COLLECTION_NAME = "clientCollection";

    public static void main(String[] args) {
        SpringApplication.run(CommunitySpringbootMongodbSyncApp.class, args);
    }

    @Bean
    public ConnectionDriver<?> connectionDriver(MongoClient mongoClient) {
        return new MongoSync4Driver(mongoClient, DATABASE_NAME);
    }

    @Bean
    public MongoDatabase mongoDatabase(MongoClient mongoClient) {
        return mongoClient.getDatabase(DATABASE_NAME);
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