package io.flamingock.examples.community.events;

import io.flamingock.core.springboot.v2.event.SpringStartedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;

public class StartedEventListener implements ApplicationListener<SpringStartedEvent> {
    private final Logger logger = LoggerFactory.getLogger(StartedEventListener.class);
    public boolean executed = false;

    @Override
    public void onApplicationEvent(SpringStartedEvent event) {
        executed = true;
        logger.info("Flamingock started....");
    }
}
