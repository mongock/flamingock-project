package io.flamingock.examples.community.couchbase;

import io.flamingock.community.runner.springboot.v2.EnableFlamingock;
import io.flamingock.examples.community.couchbase.events.FailureEventListener;
import io.flamingock.examples.community.couchbase.events.StartedEventListener;
import io.flamingock.examples.community.couchbase.events.SuccessEventListener;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@EnableFlamingock
@SpringBootApplication
public class CommunitySpringbootV2CouchbaseApp {

    public static void main(String[] args) {
        SpringApplication.run(CommunitySpringbootV2CouchbaseApp.class, args);
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