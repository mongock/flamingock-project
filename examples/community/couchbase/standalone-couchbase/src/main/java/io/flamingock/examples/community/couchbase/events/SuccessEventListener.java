package io.flamingock.examples.community.couchbase.events;


import io.flamingock.core.event.model.CompletedEvent;

import java.util.function.Consumer;

public class SuccessEventListener implements Consumer<CompletedEvent> {

    public static boolean executed = false;

    @Override
    public void accept(CompletedEvent event) {
        executed = true;
    }
}
