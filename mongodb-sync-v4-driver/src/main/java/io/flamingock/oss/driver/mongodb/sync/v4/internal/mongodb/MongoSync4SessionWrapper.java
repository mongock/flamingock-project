package io.flamingock.oss.driver.mongodb.sync.v4.internal.mongodb;


import com.mongodb.annotations.NotThreadSafe;
import com.mongodb.client.ClientSession;
import io.flamingock.oss.driver.common.mongodb.SessionWrapper;

@NotThreadSafe
public class MongoSync4SessionWrapper implements SessionWrapper<ClientSession> {

    private final ClientSession clientSession;

    private boolean closed;


    public MongoSync4SessionWrapper(ClientSession clientSession) {
        this.clientSession = clientSession;
    }

    @Override
    public ClientSession getClientSession() {
        return clientSession;
    }

    @Override
    public boolean isClosed() {
        return closed;
    }

    @Override
    public synchronized void close() {
        closed = true;
        clientSession.close();
    }
}
