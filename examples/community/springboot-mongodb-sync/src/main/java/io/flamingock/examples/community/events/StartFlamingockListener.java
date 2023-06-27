package io.flamingock.examples.community.events;

import io.flamingock.core.spring.event.SpringMigrationStartedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;

public class StartFlamingockListener implements ApplicationListener<SpringMigrationStartedEvent> {
    private final Logger logger = LoggerFactory.getLogger(StartFlamingockListener.class);
    public boolean executed = false;

    @Override
    public void onApplicationEvent(SpringMigrationStartedEvent event) {
        executed = true;
        logger.info("Flamingock started....");
    }
}
