package io.flamingock.oss.driver.mongodb.sync.v4.internal.mongodb;

import com.mongodb.annotations.NotThreadSafe;
import com.mongodb.client.ClientSession;
import com.mongodb.client.MongoClient;
import io.flamingock.oss.driver.common.mongodb.SessionManager;
import io.flamingock.oss.driver.common.mongodb.SessionWrapper;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@NotThreadSafe
public class MongoSync4SessionManager implements SessionManager<ClientSession> {

    private final MongoClient mongoClient;

    private final Map<String, MongoSync4SessionWrapper> sessionMap;

    public MongoSync4SessionManager(MongoClient mongoClient) {
        this.mongoClient = mongoClient;
        sessionMap = new HashMap<>();
    }

    /**
     * Starts a session, if not already created or is closed. Otherwise, it returns the existing one.
     *
     * @param sessionId ClientSession identifier. Will be the taskId most of the time(if not always)
     * @return ClientSession
     */
    @Override
    public SessionWrapper<ClientSession> startSession(String sessionId) {
        return sessionMap.compute(sessionId, (k, currentSession) ->
                currentSession == null || currentSession.isClosed()
                        ? new MongoSync4SessionWrapper(mongoClient.startSession())
                        : currentSession
        );
    }

    @Override
    public Optional<SessionWrapper<ClientSession>> getSession(String sessionId) {
        return Optional.ofNullable(sessionMap.get(sessionId));
    }
}
