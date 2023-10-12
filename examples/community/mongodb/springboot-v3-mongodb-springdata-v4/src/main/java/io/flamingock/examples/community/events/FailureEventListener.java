package io.flamingock.examples.community.events;

import io.flamingock.core.springboot.v3.event.SpringPipelineFailedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;

public class FailureEventListener implements ApplicationListener<SpringPipelineFailedEvent> {

    private final Logger logger = LoggerFactory.getLogger(FailureEventListener.class);

    public boolean executed = false;

    @Override
    public void onApplicationEvent(SpringPipelineFailedEvent event) {
        executed = true;
        logger.info("Flamingock failed....");
    }
}
