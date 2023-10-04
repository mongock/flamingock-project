package io.flamingock.examples.community.events;

import io.flamingock.core.event.MigrationStartedEvent;

import java.util.function.Consumer;

public class StartedEventListener implements Consumer<MigrationStartedEvent> {

    public static boolean executed = false;

    @Override
    public void accept(MigrationStartedEvent migrationStartedEvent) {
        executed = true;
    }
}
