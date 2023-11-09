package io.flamingock.core.event.model;

public interface IStageFailedEvent extends Event {


    Exception getException();


}
