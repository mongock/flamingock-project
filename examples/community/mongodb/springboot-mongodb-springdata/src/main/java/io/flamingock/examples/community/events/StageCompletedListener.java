package io.flamingock.examples.community.events;

import io.flamingock.springboot.v2.event.SpringStageCompletedEvent;
import org.springframework.context.ApplicationListener;

public class StageCompletedListener implements ApplicationListener<SpringStageCompletedEvent> {
    public int executed = 0;

    @Override
    public void onApplicationEvent(SpringStageCompletedEvent event) {
        executed++;
    }
}
