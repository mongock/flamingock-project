package io.flamingock.core.lock;

import java.util.Optional;

public abstract class LockAcquisition implements AutoCloseable {

    private final boolean required;

    public LockAcquisition(boolean required) {
        this.required = required;
    }

    public final boolean isNotRequired() {
        return !required;
    }

    public abstract Optional<Lock> lock();

    public static class Acquired extends LockAcquisition {
        private final Lock lock;


        public Acquired(Lock lock) {
            super(true);
            this.lock = lock;
        }

        public Optional<Lock> lock() {
            return Optional.of(lock);
        }


        @Override
        public void close() {
            lock.close();
        }


    }

    public static class NoRequired extends LockAcquisition {

        public NoRequired() {
            super(false);
        }

        public Optional<Lock> lock() {
            return Optional.empty();
        }

        @Override
        public void close() {

        }
    }
}
