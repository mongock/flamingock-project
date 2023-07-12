package io.flamingock.examples.community.events;

import io.flamingock.core.spring.event.SpringMigrationFailureEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;

public class FailureEventListener implements ApplicationListener<SpringMigrationFailureEvent> {

    private final Logger logger = LoggerFactory.getLogger(FailureEventListener.class);

    public boolean executed = false;

    @Override
    public void onApplicationEvent(SpringMigrationFailureEvent event) {
        executed = true;
        logger.info("Flamingock failed....");
    }
}
