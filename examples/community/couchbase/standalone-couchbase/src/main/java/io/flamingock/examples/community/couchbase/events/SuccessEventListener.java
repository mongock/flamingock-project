package io.flamingock.examples.community.couchbase.events;


import io.flamingock.core.event.model.PipelineCompletedEvent;

import java.util.function.Consumer;

public class SuccessEventListener implements Consumer<PipelineCompletedEvent> {

    public static boolean executed = false;

    @Override
    public void accept(PipelineCompletedEvent event) {
        executed = true;
    }
}
