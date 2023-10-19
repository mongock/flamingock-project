package io.flamingock.examples.community.couchbase.events;

import io.flamingock.core.springboot.v2.event.SpringPipelineCompletedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;

public class PipelineCompletedEventListener implements ApplicationListener<SpringPipelineCompletedEvent> {
    private final Logger logger = LoggerFactory.getLogger(PipelineCompletedEventListener.class);
    public boolean executed = false;

    @Override
    public void onApplicationEvent(SpringPipelineCompletedEvent event) {
        executed = true;
        logger.info("Flamingock succeeded....");
    }
}
