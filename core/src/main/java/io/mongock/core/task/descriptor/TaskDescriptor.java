package io.mongock.core.task.descriptor;

public interface TaskDescriptor {

    String getId();

    boolean isRunAlways();

    String getClassImplementor();


    default String pretty() {
        //todo improve this
        return String.format("%s ", getId());
    }



}
