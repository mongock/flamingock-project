package io.flamingock.examples.community.couchbase.events;

import io.flamingock.springboot.v2.event.SpringPipelineStartedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;

public class PipelineStartedEventListener implements ApplicationListener<SpringPipelineStartedEvent> {
    private final Logger logger = LoggerFactory.getLogger(PipelineStartedEventListener.class);
    public boolean executed = false;

    @Override
    public void onApplicationEvent(SpringPipelineStartedEvent event) {
        executed = true;
        logger.info("Flamingock started....");
    }
}
