package io.flamingock.examples.community.couchbase.events;

import io.flamingock.core.springboot.v2.event.SpringCompletedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;

public class SuccessEventListener implements ApplicationListener<SpringCompletedEvent> {
    private final Logger logger = LoggerFactory.getLogger(SuccessEventListener.class);
    public boolean executed = false;

    @Override
    public void onApplicationEvent(SpringCompletedEvent event) {
        executed = true;
        logger.info("Flamingock succeeded....");
    }
}
