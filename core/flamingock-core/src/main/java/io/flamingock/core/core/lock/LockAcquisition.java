package io.flamingock.core.core.lock;

public abstract class LockAcquisition implements AutoCloseable {

    private final boolean required;
    private final boolean acquired;

    public LockAcquisition(boolean required, boolean acquired) {
        this.required = required;
        this.acquired = acquired;
    }

    public boolean isRequired() {
        return required;
    }
    public boolean isAcquired() {
        return acquired;
    }

    public static class Acquired extends LockAcquisition {
        private final Lock lock;


        public Acquired(Lock lock) {
            super(true, true);
            this.lock = lock;
        }

        public Lock getLock() {
            return lock;
        }


        @Override
        public void close() {
            lock.close();
        }

        @Override
        public boolean isRequired() {
            return true;
        }

        @Override
        public boolean isAcquired() {
            return true;
        }
    }

    public static class NoRequired extends LockAcquisition {

        public NoRequired() {
            super(false, false);
        }

        @Override
        public void close() {

        }
    }
}
