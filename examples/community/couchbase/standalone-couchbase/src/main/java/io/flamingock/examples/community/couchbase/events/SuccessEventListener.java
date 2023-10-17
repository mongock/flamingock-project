package io.flamingock.examples.community.couchbase.events;


import io.flamingock.core.event.model.IPipelineCompletedEvent;

import java.util.function.Consumer;

public class SuccessEventListener implements Consumer<IPipelineCompletedEvent> {

    public static boolean executed = false;

    @Override
    public void accept(IPipelineCompletedEvent event) {
        executed = true;
    }
}
