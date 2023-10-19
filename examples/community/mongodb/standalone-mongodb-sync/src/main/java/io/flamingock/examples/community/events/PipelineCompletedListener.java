package io.flamingock.examples.community.events;


import io.flamingock.core.event.model.IPipelineCompletedEvent;

import java.util.function.Consumer;

public class PipelineCompletedListener implements Consumer<IPipelineCompletedEvent> {

    public static boolean executed = false;

    @Override
    public void accept(IPipelineCompletedEvent event) {
        executed = true;
    }
}
