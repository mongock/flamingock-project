package io.mongock.core.runner;

public interface Runner extends Runnable {
    void run();

    default void execute() {
        run();
    }

}
