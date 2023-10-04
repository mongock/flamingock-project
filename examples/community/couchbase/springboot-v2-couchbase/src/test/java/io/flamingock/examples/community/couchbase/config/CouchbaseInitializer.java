package io.flamingock.examples.community.couchbase.config;

import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.testcontainers.couchbase.BucketDefinition;
import org.testcontainers.couchbase.CouchbaseContainer;
import org.testcontainers.junit.jupiter.Container;

@Configuration
public class CouchbaseInitializer  implements ApplicationContextInitializer<ConfigurableApplicationContext> {

    public static final String BUCKET_NAME = "bucket";

    @Container
    public static final CouchbaseContainer container = new CouchbaseContainer("couchbase/server").withBucket(new BucketDefinition(BUCKET_NAME));

    @Override
    public void initialize(ConfigurableApplicationContext context) {
        container.withStartupAttempts(3).start();
        TestPropertyValues
            .of("spring.data.couchbase.bucket-name=" + BUCKET_NAME,
                "spring.couchbase.connection-string=" + container.getConnectionString(),
                "spring.couchbase.username=" + container.getUsername(),
                "spring.couchbase.password=" + container.getPassword(),
                "flamingock.couchbase.scope=",
                "flamingock.couchbase.collection=")
            .applyTo(context.getEnvironment());
    }
}

