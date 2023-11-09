package io.flamingock.examples.community.events;

import io.flamingock.springboot.v2.event.SpringPipelineFailedEvent;
import org.springframework.context.ApplicationListener;

public class PipelineFailedListener implements ApplicationListener<SpringPipelineFailedEvent> {

    public boolean executed = false;

    @Override
    public void onApplicationEvent(SpringPipelineFailedEvent event) {
        executed = true;
    }
}
