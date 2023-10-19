package io.flamingock.examples.community.couchbase.events;

import io.flamingock.core.springboot.v2.event.SpringPipelineFailedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;

public class PipelineFailedEventListener implements ApplicationListener<SpringPipelineFailedEvent> {

    private final Logger logger = LoggerFactory.getLogger(PipelineFailedEventListener.class);

    public boolean executed = false;

    @Override
    public void onApplicationEvent(SpringPipelineFailedEvent event) {
        executed = true;
        logger.info("Flamingock failed....");
    }
}
