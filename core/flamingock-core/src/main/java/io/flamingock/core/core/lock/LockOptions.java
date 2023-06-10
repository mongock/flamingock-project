package io.flamingock.core.core.lock;

import java.net.Inet4Address;
import java.net.UnknownHostException;
import java.util.UUID;


public class LockOptions {

    public static Builder builder() {
        return new Builder();
    }

    private final boolean withDaemon;
    private final String owner;

    private LockOptions(boolean withDaemon, String owner) {
        this.withDaemon = withDaemon;
        this.owner = owner;
    }

    public boolean isWithDaemon() {
        return withDaemon;
    }

    public String getOwner() {
        return owner;
    }


    public static class Builder {
        private boolean withDaemon = true;
        private String owner = null;

        public Builder withDaemon(boolean withDaemon) {
            this.withDaemon = withDaemon;
            return this;
        }

        public Builder setOwner(String owner) {
            this.owner = owner;
            return this;
        }

        public LockOptions build() {
            return new LockOptions(withDaemon, owner != null ? owner : generateDefaultOwner());
        }


        private static String generateDefaultOwner() {
            try {
                return Inet4Address.getLocalHost().getHostName() + UUID.randomUUID();
            } catch (final UnknownHostException e) {
                return UUID.randomUUID().toString();
            }
        }
    }
}
