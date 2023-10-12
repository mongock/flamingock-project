package io.flamingock.core.event.model;

public interface FailedEvent extends Event {


    Exception getException();

    @Override
    default boolean isSuccess() {
        return false;
    }

}
