package io.flamingock.examples.community.mongodb.sync.events;

import io.flamingock.core.core.event.MigrationSuccessEvent;

import java.util.function.Consumer;

public class SuccessEventListener implements Consumer<MigrationSuccessEvent> {

    public static boolean executed = false;

    @Override
    public void accept(MigrationSuccessEvent migrationStartedEvent) {
        executed = true;
    }
}
