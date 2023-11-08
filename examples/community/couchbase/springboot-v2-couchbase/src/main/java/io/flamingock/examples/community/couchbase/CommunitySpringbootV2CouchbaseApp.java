package io.flamingock.examples.community.couchbase;

import io.flamingock.core.springboot.v2.context.EnableFlamingock;
import io.flamingock.examples.community.couchbase.events.PipelineFailedEventListener;
import io.flamingock.examples.community.couchbase.events.PipelineStartedEventListener;
import io.flamingock.examples.community.couchbase.events.PipelineCompletedEventListener;

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
    public PipelineStartedEventListener pipelineStartedEventListener() {
        return new PipelineStartedEventListener();
    }

    @Bean
    public PipelineCompletedEventListener pipelineCompletedEventListener() {
        return new PipelineCompletedEventListener();
    }

    @Bean
    public PipelineFailedEventListener pipelineFailedEventListener() {
        return new PipelineFailedEventListener();
    }

}