package io.flamingock.core.core.lock;

public abstract class LockAcquisition implements AutoCloseable {

    public static class Acquired extends LockAcquisition {
        private final Lock lock;


        public Acquired(Lock lock) {
            this.lock = lock;
        }

        public Lock getLock() {
            return lock;
        }


        @Override
        public void close() throws Exception {
            lock.close();
        }
    }

    public static class NoRequired extends LockAcquisition {

        @Override
        public void close() {

        }
    }
}
