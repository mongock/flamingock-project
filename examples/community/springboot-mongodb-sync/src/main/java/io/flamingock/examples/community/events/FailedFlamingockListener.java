package io.flamingock.examples.community.events;

import io.flamingock.core.spring.event.SpringMigrationFailureEvent;
import io.flamingock.core.spring.event.SpringMigrationSuccessEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;

public class FailedFlamingockListener implements ApplicationListener<SpringMigrationFailureEvent> {

    private final Logger logger = LoggerFactory.getLogger(FailedFlamingockListener.class);

    public boolean executed = false;

    @Override
    public void onApplicationEvent(SpringMigrationFailureEvent event) {
        executed = true;
        logger.info("Flamingock failed....");
    }
}
