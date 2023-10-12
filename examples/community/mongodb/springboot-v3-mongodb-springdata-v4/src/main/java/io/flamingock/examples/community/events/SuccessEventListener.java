package io.flamingock.examples.community.events;

import io.flamingock.core.springboot.v3.event.SpringPipelineCompletedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;

public class SuccessEventListener implements ApplicationListener<SpringPipelineCompletedEvent> {
    private final Logger logger = LoggerFactory.getLogger(SuccessEventListener.class);
    public boolean executed = false;

    @Override
    public void onApplicationEvent(SpringPipelineCompletedEvent event) {
        executed = true;
        logger.info("Flamingock succeeded....");
    }
}
