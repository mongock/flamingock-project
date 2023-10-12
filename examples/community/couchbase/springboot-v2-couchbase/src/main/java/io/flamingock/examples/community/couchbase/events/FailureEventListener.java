package io.flamingock.examples.community.couchbase.events;

import io.flamingock.core.springboot.v2.event.SpringFailedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;

public class FailureEventListener implements ApplicationListener<SpringFailedEvent> {

    private final Logger logger = LoggerFactory.getLogger(FailureEventListener.class);

    public boolean executed = false;

    @Override
    public void onApplicationEvent(SpringFailedEvent event) {
        executed = true;
        logger.info("Flamingock failed....");
    }
}
