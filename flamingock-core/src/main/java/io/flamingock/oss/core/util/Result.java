package io.flamingock.oss.core.util;

public abstract class Result {

    private static Ok okInstance;

    public static Ok OK() {
        if (okInstance == null) {
            okInstance = new Ok();
        }
        return okInstance;
    }


    private Result() {
    }

    public final boolean isOk() {
        return this instanceof Ok;
    }


    public static class Ok extends Result {
        public Ok() {
            super();
        }

    }

    public static class Error extends Result {

        private final Throwable throwable;

        public Error(Throwable throwable) {
            super();
            this.throwable = throwable;
        }


        public Throwable getError() {
            return throwable;
        }
    }
}
