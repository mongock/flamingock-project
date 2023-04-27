package io.mongock.core.audit.domain;

import io.mongock.core.util.Failed;

public abstract class AuditResult {

    private static Ok okInstance;
    
    public static Ok OK() {
        if(okInstance == null) {
            okInstance = new Ok();
        }
        return okInstance;
    }
    

    private AuditResult() {
    }

    public final boolean isOk() {
        return this instanceof Ok;
    }


    public static class Ok extends AuditResult {
        public Ok() {
            super();
        }

    }

    public static class Error extends AuditResult implements Failed {

        private final Throwable throwable;

        public Error(Throwable throwable) {
            super();
            this.throwable = throwable;
        }

        @Override
        public Throwable getError() {
            return throwable;
        }
    }
}
