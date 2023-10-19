package io.flamingock.examples.community.events;

import io.flamingock.core.event.model.IStageCompletedEvent;

import java.util.function.Consumer;

public class StageCompletedListener implements Consumer<IStageCompletedEvent> {

    public static int executed = 0;
    @Override
    public void accept(IStageCompletedEvent iStageCompletedEvent) {
        executed++;
    }
}
