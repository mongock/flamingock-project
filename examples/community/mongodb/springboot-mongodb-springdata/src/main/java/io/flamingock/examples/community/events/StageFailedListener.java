package io.flamingock.examples.community.events;

import io.flamingock.core.springboot.v2.event.SpringPipelineFailedEvent;
import io.flamingock.core.springboot.v2.event.SpringStageFailedEvent;
import org.springframework.context.ApplicationListener;

public class StageFailedListener implements ApplicationListener<SpringStageFailedEvent> {

    public int executed = 0;

    @Override
    public void onApplicationEvent(SpringStageFailedEvent event) {
        executed++;
    }
}
