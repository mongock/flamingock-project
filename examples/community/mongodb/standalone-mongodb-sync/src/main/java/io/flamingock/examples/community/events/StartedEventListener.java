package io.flamingock.examples.community.events;

import io.flamingock.core.event.model.StartedEvent;

import java.util.function.Consumer;

public class StartedEventListener implements Consumer<StartedEvent> {

    public static boolean executed = false;

    @Override
    public void accept(StartedEvent migrationStartedEvent) {
        executed = true;
    }
}
