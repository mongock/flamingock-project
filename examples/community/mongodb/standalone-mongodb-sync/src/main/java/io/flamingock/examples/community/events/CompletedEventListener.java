package io.flamingock.examples.community.events;


import io.flamingock.core.event.model.PipelineCompletedEvent;

import java.util.function.Consumer;

public class CompletedEventListener implements Consumer<PipelineCompletedEvent> {

    public static boolean executed = false;

    @Override
    public void accept(PipelineCompletedEvent event) {
        executed = true;
    }
}
