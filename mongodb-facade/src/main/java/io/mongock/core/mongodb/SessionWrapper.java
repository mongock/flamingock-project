package io.mongock.core.mongodb;

import java.io.Closeable;

public interface SessionWrapper<CLIENT_SESSION> extends Closeable {


    CLIENT_SESSION getClientSession();

    boolean isClosed();

    void close();
}