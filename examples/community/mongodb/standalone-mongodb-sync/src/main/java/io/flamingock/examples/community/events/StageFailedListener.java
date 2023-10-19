package io.flamingock.examples.community.events;

import io.flamingock.core.event.model.IStageFailedEvent;
import io.flamingock.core.event.model.IStageStartedEvent;

import java.util.function.Consumer;

public class StageFailedListener implements Consumer<IStageFailedEvent> {
    public static int executed = 0;
    @Override
    public void accept(IStageFailedEvent event) {
        executed++;
    }
}
