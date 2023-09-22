package io.flamingock.examples.community.couchbase.events;

import io.flamingock.core.springboot.v2.event.SpringMigrationStartedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;

public class StartedEventListener implements ApplicationListener<SpringMigrationStartedEvent> {
    private final Logger logger = LoggerFactory.getLogger(StartedEventListener.class);
    public boolean executed = false;

    @Override
    public void onApplicationEvent(SpringMigrationStartedEvent event) {
        executed = true;
        logger.info("Flamingock started....");
    }
}
