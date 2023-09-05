package io.flamingock.examples.community.config;

import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.utility.DockerImageName;

import java.util.Collections;
import java.util.List;

@Configuration
public class MongoInitializer  implements ApplicationContextInitializer<ConfigurableApplicationContext> {

    @Container
    public static final MongoDBContainer mongoDBContainer = new MongoDBContainer(DockerImageName.parse("mongo:4.0.10"));

    @Override
    public void initialize(ConfigurableApplicationContext context) {
        mongoDBContainer.start();
        List<String> addedProperties = Collections.singletonList(
                "spring.data.mongodb.uri=" + mongoDBContainer.getReplicaSetUrl()
        );
        TestPropertyValues.of(addedProperties).applyTo(context.getEnvironment());
    }
}

