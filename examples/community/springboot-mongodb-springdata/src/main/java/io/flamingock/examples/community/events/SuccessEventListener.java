package io.flamingock.examples.community.events;

import io.flamingock.core.spring.event.SpringMigrationSuccessEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;

public class SuccessEventListener implements ApplicationListener<SpringMigrationSuccessEvent> {
    private final Logger logger = LoggerFactory.getLogger(SuccessEventListener.class);
    public boolean executed = false;

    @Override
    public void onApplicationEvent(SpringMigrationSuccessEvent event) {
        executed = true;
        logger.info("Flamingock succeeded....");
    }
}
