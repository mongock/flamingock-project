package io.flamingock.core.event;


import io.flamingock.core.event.model.Event;
import io.flamingock.core.event.model.PipelineCompletedEvent;
import io.flamingock.core.event.model.PipelineFailedEvent;
import io.flamingock.core.event.model.PipelineIgnoredEvent;
import io.flamingock.core.event.model.PipelineStartedEvent;
import io.flamingock.core.util.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.TypeVariable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

public class EventPublisher {

    private static final Logger logger = LoggerFactory.getLogger(EventPublisher.class);

    private final List<Pair<Class<? extends Event>, Consumer<? extends Event>>> listeners = new ArrayList<>();

    public EventPublisher() {
    }

    public EventPublisher listenPipelineStarted(Consumer<PipelineStartedEvent> listener) {
        return addListener(PipelineStartedEvent.class, listener);
    }

    public EventPublisher listenPipelineCompleted(Consumer<PipelineCompletedEvent> listener) {
        return addListener(PipelineCompletedEvent.class, listener);

    }

    public EventPublisher listenPipelineIgnored(Consumer<PipelineIgnoredEvent> listener) {
        return addListener(PipelineIgnoredEvent.class, listener);

    }

    public EventPublisher listenPipelineFailed(Consumer<PipelineFailedEvent> listener) {
        return addListener(PipelineFailedEvent.class, listener);

    }

    private EventPublisher addListener(Class<? extends Event> eventType, Consumer<? extends Event> listener) {
        if(listener != null) {
            listeners.add(new Pair<>(eventType, listener));
        }
        return this;
    }

    public void publish(Event event) {
        Consumer<Event> eventConsumer = getListener(event.getClass())
                .orElse(event1 -> logger.debug("No registered listener for Event[{}]", event1.toString()));
        eventConsumer.accept(event);
    }


    //We ensure programmatically that the type of event is aligned with the consumer,
    //although we return a generic Consumer<Event>
    @SuppressWarnings("unchecked")
    private <T extends Event> Optional<Consumer<Event>> getListener(Class<T> eventType) {
        return listeners
                .stream()
                .filter(pair -> pair.getFirst().isAssignableFrom(eventType))
                .map(Pair::getSecond)
                .map(consumer -> (Consumer<Event>) consumer)
                .findFirst();
    }
}
